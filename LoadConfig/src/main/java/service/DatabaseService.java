package service;

import model.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    public List<DatabaseConfig> getActiveDatabases(Connection conn) throws SQLException {
        List<DatabaseConfig> dbList = new ArrayList<>();
        String sql = "SELECT * FROM cfg_database WHERE isActive = 1";

        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                DatabaseConfig db = new DatabaseConfig();
                db.setId(rs.getInt("id"));
                db.setName(rs.getString("name"));
                db.setType(rs.getInt("type"));
                db.setHost(rs.getString("host"));
                db.setPort(rs.getString("port"));
                db.setUsername(rs.getString("username"));
                db.setPassword(rs.getString("password"));
                dbList.add(db);
            }
        }
        return dbList;
    }
}


