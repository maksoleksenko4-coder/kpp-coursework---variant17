package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {

    public void add(Observation obs) throws SQLException {
        String sql = "INSERT INTO observation (field_id, date, description, f1_score, f2_score, f3_score) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, obs.getFieldId());
            p.setString(2, obs.getDate());
            p.setString(3, obs.getDescription());
            p.setDouble(4, obs.getF1Score());
            p.setDouble(5, obs.getF2Score());
            p.setDouble(6, obs.getF3Score());
            p.executeUpdate();
        }
    }

    public List<Observation> getAll() throws SQLException {
        List<Observation> list = new ArrayList<>();
        String sql = "SELECT id, field_id, date, description, f1_score, f2_score, f3_score FROM observation ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(new Observation(
                        rs.getLong("id"),
                        rs.getLong("field_id"),
                        rs.getString("date"),
                        rs.getString("description"),
                        rs.getDouble("f1_score"),
                        rs.getDouble("f2_score"),
                        rs.getDouble("f3_score")
                ));
            }
        }
        return list;
    }

    public Observation getById(long id) throws SQLException {
        String sql = "SELECT id, field_id, date, description, f1_score, f2_score, f3_score FROM observation WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Observation(
                            rs.getLong("id"),
                            rs.getLong("field_id"),
                            rs.getString("date"),
                            rs.getString("description"),
                            rs.getDouble("f1_score"),
                            rs.getDouble("f2_score"),
                            rs.getDouble("f3_score")
                    );
                }
            }
        }
        return null;
    }

    public void update(Observation obs) throws SQLException {
        String sql = "UPDATE observation SET field_id=?, date=?, description=?, f1_score=?, f2_score=?, f3_score=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, obs.getFieldId());
            p.setString(2, obs.getDate());
            p.setString(3, obs.getDescription());
            p.setDouble(4, obs.getF1Score());
            p.setDouble(5, obs.getF2Score());
            p.setDouble(6, obs.getF3Score());
            p.setLong(7, obs.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM observation WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}
