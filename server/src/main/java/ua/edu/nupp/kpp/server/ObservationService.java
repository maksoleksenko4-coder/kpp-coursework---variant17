package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ObservationService {

    private static final Logger LOG = Logger.getLogger(ObservationService.class.getName());
    private final ObservationDAO dao = new ObservationDAO();
    private final FieldDAO fieldDAO = new FieldDAO();

    private void validate(Observation obs) {
        if (obs == null) {
            LOG.warning("Відхилено операцію: об'єкт спостереження порожній");
            throw new IllegalArgumentException("Об'єкт спостереження не може бути порожнім");
        }
        if (obs.getFieldId() <= 0) {
            LOG.warning("Відхилено операцію: некоректний ID поля");
            throw new IllegalArgumentException("Некоректний ID поля");
        }
        if (obs.getDate() == null || obs.getDate().isBlank()) {
            LOG.warning("Відхилено операцію: порожня дата спостереження");
            throw new IllegalArgumentException("Дата є обов'язковою");
        }
        if (obs.getDescription() == null || obs.getDescription().isBlank()) {
            LOG.warning("Відхилено операцію: порожній опис спостереження");
            throw new IllegalArgumentException("Опис є обов'язковим");
        }
        if (obs.getF1Score() < 0 || obs.getF1Score() > 100 ||
                obs.getF2Score() < 0 || obs.getF2Score() > 100 ||
                obs.getF3Score() < 0 || obs.getF3Score() > 100) {
            LOG.warning("Відхилено операцію: фактори виходять за діапазон 0..100");
            throw new IllegalArgumentException("Показники (F1, F2, F3) мають бути в діапазоні 0..100");
        }
    }

    private void requireField(long fieldId) throws SQLException {
        if (fieldDAO.getById(fieldId) == null) {
            LOG.warning("Поле не знайдено: ID=" + fieldId);
            throw new IllegalArgumentException("Поле з таким ID не знайдено в базі даних");
        }
    }

    public void add(Observation obs) throws SQLException {
        validate(obs);
        requireField(obs.getFieldId());
        dao.add(obs);
        LOG.info("Додано спостереження");
    }

    public void update(Observation obs) throws SQLException {
        validate(obs);
        requireField(obs.getFieldId());
        dao.update(obs);
        LOG.info("Оновлено спостереження");
    }

    public List<Observation> getAll() throws SQLException {
        return dao.getAll();
    }

    public Observation getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено спостереження");
    }
}