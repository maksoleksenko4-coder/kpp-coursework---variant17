package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FieldDAO {

    public void add(Field field) throws SQLException {
        String sql = "INSERT INTO field (name, crop_type, area) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, field.getName());
            p.setString(2, field.getCropType());
            p.setDouble(3, field.getArea());
            p.executeUpdate();
        }
    }

    public List<Field> getAll() throws SQLException {
        List<Field> list = new ArrayList<>();
        String sql = "SELECT id, name, crop_type, area FROM field ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(new Field(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("crop_type"),
                        rs.getDouble("area")
                ));
            }
        }
        return list;
    }

    public Field getById(long id) throws SQLException {
        String sql = "SELECT id, name, crop_type, area FROM field WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Field(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("crop_type"),
                            rs.getDouble("area")
                    );
                }
            }
        }
        return null;
    }

    public void update(Field field) throws SQLException {
        String sql = "UPDATE field SET name = ?, crop_type = ?, area = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, field.getName());
            p.setString(2, field.getCropType());
            p.setDouble(3, field.getArea());
            p.setLong(4, field.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM field WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}