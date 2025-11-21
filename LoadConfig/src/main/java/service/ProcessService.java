package service;

import model.Process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessService {

    public List<Process> getAllProcesses(Connection conn) throws SQLException {
        List<Process> list = new ArrayList<>();
        String sql = "SELECT id, name, description, scriptName, typeProcess FROM process ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Process(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("scriptName"),
                        rs.getString("typeProcess")
                ));
            }
        }
        return list;
    }
}
