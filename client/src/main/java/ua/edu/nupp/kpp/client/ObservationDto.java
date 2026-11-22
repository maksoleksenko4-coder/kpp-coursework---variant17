package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ObservationDto {
    private Long id;
    private long fieldId;
    private String date;
    private String description;
    private double f1Score;
    private double f2Score;
    private double f3Score;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getFieldId() { return fieldId; }
    public void setFieldId(long fieldId) { this.fieldId = fieldId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }

    public double getF2Score() { return f2Score; }
    public void setF2Score(double f2Score) { this.f2Score = f2Score; }

    public double getF3Score() { return f3Score; }
    public void setF3Score(double f3Score) { this.f3Score = f3Score; }
}