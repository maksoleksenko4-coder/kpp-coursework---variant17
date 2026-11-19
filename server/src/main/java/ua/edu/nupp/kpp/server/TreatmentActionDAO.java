package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentActionDAO {

    public void add(TreatmentAction action) throws SQLException {
        String sql = "INSERT INTO treatment_action (problem_id, description, planned_date) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, action.getProblemId());
            p.setString(2, action.getDescription());
            p.setString(3, action.getPlannedDate());
            p.executeUpdate();
        }
    }

    public List<TreatmentAction> getAll() throws SQLException {
        List<TreatmentAction> list = new ArrayList<>();
        String sql = "SELECT id, problem_id, description, planned_date FROM treatment_action ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(new TreatmentAction(
                        rs.getLong("id"),
                        rs.getLong("problem_id"),
                        rs.getString("description"),
                        rs.getString("planned_date")
                ));
            }
        }
        return list;
    }

    public TreatmentAction getById(long id) throws SQLException {
        String sql = "SELECT id, problem_id, description, planned_date FROM treatment_action WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new TreatmentAction(
                            rs.getLong("id"),
                            rs.getLong("problem_id"),
                            rs.getString("description"),
                            rs.getString("planned_date")
                    );
                }
            }
        }
        return null;
    }

    public void update(TreatmentAction action) throws SQLException {
        String sql = "UPDATE treatment_action SET problem_id=?, description=?, planned_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, action.getProblemId());
            p.setString(2, action.getDescription());
            p.setString(3, action.getPlannedDate());
            p.setLong(4, action.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM treatment_action WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}