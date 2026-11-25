package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemDto {
    private Long id;
    private long observationId;
    private String state;

    @JsonProperty("f1")
    private double f1Score;

    @JsonProperty("f2")
    private double f2Score;

    @JsonProperty("f3")
    private double f3Score;

    private double riskIndex;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getObservationId() { return observationId; }
    public void setObservationId(long observationId) { this.observationId = observationId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }

    public double getF2Score() { return f2Score; }
    public void setF2Score(double f2Score) { this.f2Score = f2Score; }

    public double getF3Score() { return f3Score; }
    public void setF3Score(double f3Score) { this.f3Score = f3Score; }

    public double getRiskIndex() { return riskIndex; }
    public void setRiskIndex(double riskIndex) { this.riskIndex = riskIndex; }
}
