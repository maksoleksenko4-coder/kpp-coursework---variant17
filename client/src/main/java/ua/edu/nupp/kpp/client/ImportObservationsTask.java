package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportObservationsTask extends Task<Integer> {

    private static final Logger LOG = Logger.getLogger(ImportObservationsTask.class.getName());
    private static final String EXPECTED_HEADER = "field_id;date;description;f1_score;f2_score;f3_score";

    private final Path file;
    private final ApiClient api;

    public ImportObservationsTask(Path file, ApiClient api) {
        this.file = file;
        this.api = api;
    }

    @Override
    protected Integer call() throws Exception {
        LOG.info("Start of CSV import: " + file.getFileName());
        updateMessage("Reading a CSV file...");

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("The CSV file is empty.");
        }

        String header = lines.get(0).replace("\uFEFF", "").strip();
        if (!EXPECTED_HEADER.equals(header)) {
            throw new IllegalArgumentException("Invalid CSV header. Expected: " + EXPECTED_HEADER);
        }

        int total = (int) lines.stream().skip(1).filter(s -> !s.isBlank()).count();
        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("No rows to import");
            LOG.info("The CSV contains no data rows.");
            return 0;
        }

        int processed = 0;
        int imported = 0;
        updateProgress(0, total);

        for (int i = 1; i < lines.size(); i++) {
            if (isCancelled()) {
                LOG.warning("Import cancelled by user after processing " + processed + " lines");
                break;
            }
            if (lines.get(i).isBlank()) {
                continue;
            }

            String[] p = lines.get(i).split(";", -1);
            if (p.length != 6) {
                LOG.warning("Line skipped " + (i + 1) + ": 6 fields expected, received " + p.length);
            } else {
                try {
                    long fieldId = Long.parseLong(p[0].strip());
                    String date = p[1].strip();
                    String description = p[2].strip();
                    double f1 = Double.parseDouble(p[3].strip());
                    double f2 = Double.parseDouble(p[4].strip());
                    double f3 = Double.parseDouble(p[5].strip());

                    if (!Double.isFinite(f1) || !Double.isFinite(f2) || !Double.isFinite(f3)) {
                        LOG.warning("NaN або Infinity у рядку " + (i + 1));
                        throw new IllegalArgumentException("Invalid numeric value in the row " + (i + 1));
                    }

                    Map<String, String> params = new LinkedHashMap<>();
                    params.put("action", "add");
                    params.put("fieldId", String.valueOf(fieldId));
                    params.put("date", date);
                    params.put("description", description);
                    params.put("f1Score", String.valueOf(f1));
                    params.put("f2Score", String.valueOf(f2));
                    params.put("f3Score", String.valueOf(f3));

                    try {
                        api.postForm("/observations", params);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "HTTP error during string import " + (i + 1), e);
                        throw e;
                    }
                    imported++;
                } catch (NumberFormatException e) {
                    LOG.warning("Invalid number in the line " + (i + 1));
                    throw new IllegalArgumentException("Invalid number in the line " + (i + 1), e);
                }
            }

            processed++;
            updateProgress(processed, total);
            updateMessage("Processed " + processed + " із " + total + "; imported " + imported);
        }

        LOG.info("Import complete. Imported: " + imported);
        return imported;
    }
}