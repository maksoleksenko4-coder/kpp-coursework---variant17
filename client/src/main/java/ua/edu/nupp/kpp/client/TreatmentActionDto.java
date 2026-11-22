package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreatmentActionDto {
    private Long id;
    private long problemId;
    private String description;
    private String plannedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getProblemId() { return problemId; }
    public void setProblemId(long problemId) { this.problemId = problemId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPlannedDate() { return plannedDate; }
    public void setPlannedDate(String plannedDate) { this.plannedDate = plannedDate; }
}