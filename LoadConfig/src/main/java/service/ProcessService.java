package service;

import dto.Process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessService {

    public List<dto.Process> getProcessList(Connection conn, int sourceId) throws SQLException {
        List<Process> list = new ArrayList<>();

        PreparedStatement st = conn.prepareStatement(
                "SELECT id, targetPath, name AS scriptName FROM cfg_source_process WHERE dataSourceId=?"
        );
        st.setInt(1, sourceId);

        ResultSet rs = st.executeQuery();

        while (rs.next()) {
            dto.Process p = new dto.Process();
            p.setId(rs.getInt("id"));
            p.setTargetPath(rs.getString("targetPath"));
            p.setScriptName( rs.getString("scriptName"));
            list.add(p);
        }

        return list;
    }
}
