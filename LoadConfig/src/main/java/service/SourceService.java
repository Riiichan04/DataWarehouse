package service;



import dto.Source;

import java.sql.*;
public class SourceService {
    public Source getActiveSource(Connection conn) throws SQLException {
        Source source = null;

        PreparedStatement st = conn.prepareStatement(
                "SELECT id, name, url FROM cfg_source WHERE isActive=1 LIMIT 1"
        );
        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            source = new Source();
            source.setSourceId(rs.getInt("id"));
            source.setName( rs.getString("name"));
            source.setUrl(rs.getString("url"));
        }

        return source;
    }

    public void createSourceProcessIfNotExist(Connection conn, int sourceId, String targetPath, String runDate) throws SQLException {
        PreparedStatement st = conn.prepareStatement(
                "INSERT INTO cfg_source_process(name, dataSourceId, targetPath, description, createdAt, updatedAt) " +
                        "SELECT ?, ?, ?, ?, NOW(), NOW() FROM dual " +
                        "WHERE NOT EXISTS (SELECT 1 FROM cfg_source_process WHERE dataSourceId=? AND targetPath=?)"
        );

        st.setString(1, "crawl_" + runDate);
        st.setInt(2, sourceId);
        st.setString(3, targetPath);
        st.setString(4, "Daily crawl process " + runDate);
        st.setInt(5, sourceId);
        st.setString(6, targetPath);

        st.executeUpdate();
    }
}
