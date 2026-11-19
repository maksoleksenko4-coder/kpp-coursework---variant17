package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class FieldService {

    private static final Logger LOG = Logger.getLogger(FieldService.class.getName());
    private final FieldDAO dao = new FieldDAO();

    private void validate(Field field) {
        if (field == null) {
            LOG.warning("Відхилено операцію: об'єкт поля порожній");
            throw new IllegalArgumentException("Об'єкт поля не може бути порожнім");
        }
        if (field.getName() == null || field.getName().isBlank()) {
            LOG.warning("Відхилено операцію: порожня назва поля");
            throw new IllegalArgumentException("Назва поля є обов'язковою");
        }
        if (field.getCropType() == null || field.getCropType().isBlank()) {
            LOG.warning("Відхилено операцію: порожній тип культури");
            throw new IllegalArgumentException("Тип культури є обов'язковим");
        }
        if (field.getArea() <= 0) {
            LOG.warning("Відхилено операцію: некоректна площа поля (" + field.getArea() + ")");
            throw new IllegalArgumentException("Площа поля повинна бути більшою за 0");
        }
    }

    public void add(Field field) throws SQLException {
        validate(field);
        dao.add(field);
        LOG.info("Додано поле");
    }

    public List<Field> getAll() throws SQLException {
        return dao.getAll();
    }

    public Field getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void update(Field field) throws SQLException {
        validate(field);
        dao.update(field);
        LOG.info("Оновлено поле");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено поле");
    }
}