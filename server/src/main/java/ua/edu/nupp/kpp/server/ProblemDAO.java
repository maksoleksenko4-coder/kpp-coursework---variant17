package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProblemDAO {

    public void add(Problem problem) throws SQLException {
        String sql = "INSERT INTO problem (observation_id, state, f1, f2, f3, risk_index) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, problem.getObservationId());
            p.setString(2, problem.getState().name());
            p.setDouble(3, problem.getF1());
            p.setDouble(4, problem.getF2());
            p.setDouble(5, problem.getF3());
            p.setDouble(6, problem.getRiskIndex());
            p.executeUpdate();
        }
    }

    public List<Problem> getAll() throws SQLException {
        List<Problem> list = new ArrayList<>();
        String sql = "SELECT id, observation_id, state, f1, f2, f3, risk_index FROM problem ORDER BY id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                list.add(new Problem(
                        rs.getLong("id"),
                        rs.getLong("observation_id"),
                        ProblemState.valueOf(rs.getString("state")),
                        rs.getDouble("f1"),
                        rs.getDouble("f2"),
                        rs.getDouble("f3"),
                        rs.getDouble("risk_index")
                ));
            }
        }
        return list;
    }

    public Problem getById(long id) throws SQLException {
        String sql = "SELECT id, observation_id, state, f1, f2, f3, risk_index FROM problem WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Problem(
                            rs.getLong("id"),
                            rs.getLong("observation_id"),
                            ProblemState.valueOf(rs.getString("state")),
                            rs.getDouble("f1"),
                            rs.getDouble("f2"),
                            rs.getDouble("f3"),
                            rs.getDouble("risk_index")
                    );
                }
            }
        }
        return null;
    }

    public void update(Problem problem) throws SQLException {
        String sql = "UPDATE problem SET observation_id=?, state=?, f1=?, f2=?, f3=?, risk_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, problem.getObservationId());
            p.setString(2, problem.getState().name());
            p.setDouble(3, problem.getF1());
            p.setDouble(4, problem.getF2());
            p.setDouble(5, problem.getF3());
            p.setDouble(6, problem.getRiskIndex());
            p.setLong(7, problem.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, ProblemState state) throws SQLException {
        String sql = "UPDATE problem SET state = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM problem WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}