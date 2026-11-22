package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ProblemServlet extends HttpServlet {
    private final ProblemService service = new ProblemService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                long observationId = parseLong(req.getParameter("observationId"), "observationId");
                service.registerProblem(observationId);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("update".equals(action)) {
                long id = parseLong(req.getParameter("id"), "id");
                long observationId = parseLong(req.getParameter("observationId"), "observationId");
                service.update(id, observationId);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("delete".equals(action)) {
                long id = parseLong(req.getParameter("id"), "id");
                service.delete(id);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("transition".equals(action)) {
                long id = parseLong(req.getParameter("id"), "id");
                ProblemState nextState = parseState(req.getParameter("state"));
                service.changeState(id, nextState);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else {
                throw new IllegalArgumentException("Невідома дія: " + action);
            }
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле " + fieldName + " обов'язкове");
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некоректне ціле число для " + fieldName);
        }
    }

    private ProblemState parseState(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле state обов'язкове");
        }
        try {
            return ProblemState.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некоректний стан проблеми: " + value);
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        int status = (e instanceof IllegalArgumentException)
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = (e.getMessage() != null && !e.getMessage().isBlank())
                ? e.getMessage()
                : "Внутрішня помилка сервера";
        writeJson(resp, status, Map.of("error", message));
    }
}