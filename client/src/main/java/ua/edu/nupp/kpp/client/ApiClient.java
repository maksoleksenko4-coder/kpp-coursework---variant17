package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/server";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<FieldDto> getFields() throws Exception {
        return getList("/fields", new TypeReference<List<FieldDto>>() {});
    }

    public List<ObservationDto> getObservations() throws Exception {
        return getList("/observations", new TypeReference<List<ObservationDto>>() {});
    }

    public List<ProblemDto> getProblems() throws Exception {
        return getList("/problems", new TypeReference<List<ProblemDto>>() {});
    }

    public List<TreatmentActionDto> getActions() throws Exception {
        return getList("/actions", new TypeReference<List<TreatmentActionDto>>() {});
    }

    public void postForm(String path, Map<String, String> params) throws Exception {
        String form = params.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue() == null ? "" : e.getValue()))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new RuntimeException(extractError(response.body(), response.statusCode()));
        }
    }

    private <T> List<T> getList(String path, TypeReference<List<T>> typeRef) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new RuntimeException(extractError(response.body(), response.statusCode()));
        }
        return mapper.readValue(response.body(), typeRef);
    }

    private String extractError(String body, int statusCode) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) {
                return node.get("error").asText();
            }
        } catch (Exception ignored) {
        }
        return "Помилка сервера (HTTP " + statusCode + "): " + body;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}