package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TreatmentActionService {

    private static final Logger LOG = Logger.getLogger(TreatmentActionService.class.getName());
    private final TreatmentActionDAO dao = new TreatmentActionDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();

    private void validate(TreatmentAction action) {
        if (action == null) {
            LOG.warning("Відхилено операцію: об'єкт заходу обробки порожній");
            throw new IllegalArgumentException("Об'єкт дії не може бути порожнім");
        }
        if (action.getProblemId() <= 0) {
            LOG.warning("Відхилено операцію: некоректний ID проблеми");
            throw new IllegalArgumentException("Некоректний ID проблеми");
        }
        if (action.getDescription() == null || action.getDescription().isBlank()) {
            LOG.warning("Відхилено операцію: порожній опис заходу обробки");
            throw new IllegalArgumentException("Опис дії є обов'язковим");
        }
        if (action.getPlannedDate() == null || action.getPlannedDate().isBlank()) {
            LOG.warning("Відхилено операцію: порожня запланована дата");
            throw new IllegalArgumentException("Запланована дата є обов'язковою");
        }
    }

    private void requireProblem(long problemId) throws SQLException {
        if (problemDAO.getById(problemId) == null) {
            LOG.warning("Проблему не знайдено: ID=" + problemId);
            throw new IllegalArgumentException("Проблема з таким ID не знайдена в базі даних");
        }
    }

    public void add(TreatmentAction action) throws SQLException {
        validate(action);
        requireProblem(action.getProblemId());
        dao.add(action);
        LOG.info("Додано захід обробки");
    }

    public void update(TreatmentAction action) throws SQLException {
        validate(action);
        requireProblem(action.getProblemId());
        dao.update(action);
        LOG.info("Оновлено захід обробки");
    }

    public List<TreatmentAction> getAll() throws SQLException {
        return dao.getAll();
    }

    public TreatmentAction getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено захід обробки");
    }
}