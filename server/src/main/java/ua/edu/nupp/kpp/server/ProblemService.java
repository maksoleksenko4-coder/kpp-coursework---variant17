package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ProblemService {

    private static final Logger LOG = Logger.getLogger(ProblemService.class.getName());

    private final ProblemDAO dao = new ProblemDAO();
    private final ObservationDAO observationDAO = new ObservationDAO();

    public List<Problem> getAll() throws SQLException {
        return dao.getAll();
    }

    public Problem getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public Problem registerProblem(long observationId) throws SQLException {
        Problem problem = fromObservation(null, observationId, ProblemState.DETECTED);
        dao.add(problem);
        LOG.info("Зареєстровано нову проблему для спостереження ID=" + observationId);

        List<Problem> problems = dao.getAll();
        return problems.get(problems.size() - 1);
    }

    public void update(long id, long observationId) throws SQLException {
        Problem old = dao.getById(id);
        if (old == null) {
            LOG.warning("Спроба оновити неіснуючу проблему ID=" + id);
            throw new IllegalArgumentException("Проблему з таким ID не знайдено");
        }
        dao.update(fromObservation(id, observationId, old.getState()));
        LOG.info("Оновлено проблему ID=" + id);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено проблему ID=" + id);
    }

    public static boolean isAllowedTransition(ProblemState from, ProblemState to) {
        return (from == ProblemState.DETECTED && to == ProblemState.CONFIRMED)
                || (from == ProblemState.CONFIRMED && to == ProblemState.TREATMENT_PLANNED)
                || (from == ProblemState.TREATMENT_PLANNED && to == ProblemState.RESOLVED);
    }

    public void changeState(long id, ProblemState next) throws SQLException {
        Problem current = dao.getById(id);
        if (current == null) {
            LOG.warning("Спроба змінити стан неіснуючої проблеми ID=" + id);
            throw new IllegalArgumentException("Проблему з таким ID не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException(
                    "Недопустимий перехід стану з " + current.getState() + " у " + next);
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан проблеми: " + current.getState() + " -> " + next);
    }

    private Problem fromObservation(Long id, long observationId, ProblemState state) throws SQLException {
        Observation obs = observationDAO.getById(observationId);
        if (obs == null) {
            LOG.warning("Спостереження не знайдено: ID=" + observationId);
            throw new IllegalArgumentException("Спостереження з таким ID не знайдено");
        }

        double f1 = obs.getF1Score();
        double f2 = obs.getF2Score();
        double f3 = obs.getF3Score();

        double riskIndex = RiskCalculator.calculate(f1, f2, f3);

        return new Problem(id, observationId, state, f1, f2, f3, riskIndex);
    }
}