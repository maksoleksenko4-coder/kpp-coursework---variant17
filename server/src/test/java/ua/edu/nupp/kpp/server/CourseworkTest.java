package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    @Test
    void riskZeroWhenAllFactorsZero() {
        assertEquals(0.0, RiskCalculator.calculate(0, 0, 0), 0.001);
    }

    @Test
    void riskMaxWhenAllFactors100() {
        assertEquals(100.0, RiskCalculator.calculate(100, 100, 100), 0.001);
    }

    @Test
    void riskForDemoObservationIs2475() {
        // 0.40 * 15.0 + 0.35 * 25.0 + 0.25 * 40.0 = 6.0 + 8.75 + 10.0 = 24.75
        assertEquals(24.75, RiskCalculator.calculate(15.0, 25.0, 40.0), 0.001);
    }

    @Test
    void riskForCsvObservationIs8775() {
        // 0.40 * 85.5 + 0.35 * 90.0 + 0.25 * 88.2 = 34.2 + 31.5 + 22.05 = 87.75
        assertEquals(87.75, RiskCalculator.calculate(85.5, 90.0, 88.2), 0.001);
    }

    @Test
    void riskRejectsFactorAbove100() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(101, 50, 50));
    }

    @Test
    void riskRejectsNegativeFactor() {
        assertThrows(IllegalArgumentException.class,
                () -> RiskCalculator.calculate(50, -1, 50));
    }

    @Test
    void detectedToConfirmedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.DETECTED, ProblemState.CONFIRMED));
    }

    @Test
    void confirmedToTreatmentPlannedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.CONFIRMED, ProblemState.TREATMENT_PLANNED));
    }

    @Test
    void treatmentPlannedToResolvedAllowed() {
        assertTrue(ProblemService.isAllowedTransition(
                ProblemState.TREATMENT_PLANNED, ProblemState.RESOLVED));
    }

    @Test
    void detectedToResolvedRejected() {
        assertFalse(ProblemService.isAllowedTransition(
                ProblemState.DETECTED, ProblemState.RESOLVED));
    }
}