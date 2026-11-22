package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class FieldServlet extends HttpServlet {
    private final FieldService service = new FieldService();
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
                Field f = new Field();
                f.setName(req.getParameter("name"));
                f.setArea(parseDouble(req.getParameter("area"), "area"));
                f.setCropType(req.getParameter("cropType"));
                service.add(f);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("update".equals(action)) {
                Field f = new Field();
                f.setId(parseLong(req.getParameter("id"), "id"));
                f.setName(req.getParameter("name"));
                f.setArea(parseDouble(req.getParameter("area"), "area"));
                f.setCropType(req.getParameter("cropType"));
                service.update(f);
                writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
            } else if ("delete".equals(action)) {
                long id = parseLong(req.getParameter("id"), "id");
                service.delete(id);
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

    private double parseDouble(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле " + fieldName + " обов'язкове");
        }
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некоректне число для " + fieldName);
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