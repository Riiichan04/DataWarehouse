package service;



import model.SourceConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SourceService {
    public List<SourceConfig> getListActiveSources(Connection conn) throws SQLException {
        List<SourceConfig> sourceConfigList = new ArrayList<>();

        // Bỏ LIMIT 1 để lấy hết các đài (Bắc, Trung, Nam...)
        String sql = "SELECT id, name, url FROM cfg_source WHERE isActive = 1";

        try (PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                SourceConfig sourceConfig = new SourceConfig();
                sourceConfig.setId(rs.getInt("id"));
                sourceConfig.setName(rs.getString("name"));
                sourceConfig.setUrl(rs.getString("url"));
                sourceConfigList.add(sourceConfig);
            }
        }
        return sourceConfigList;
    }


    /**
     * Kiểm tra xem process extract cho ngày này, nguồn này đã được cấu hình chưa.
     * @return ID của dòng trong cfg_extract_process (dùng để tham chiếu sau này)
     */
    public int ensureExtractProcessExists(Connection conn, int extractProcessDefId, int sourceId, String runDate, String baseDir) throws SQLException {
        int recordId = -1;

        String checkSql = "SELECT id FROM cfg_extract_process WHERE dataSourceId = ? AND targetPath LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, sourceId);
            ps.setString(2, "%" + runDate + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Đã tồn tại, trả về ID
            }
        }

        String targetPath = baseDir + "result_" + runDate + "_" + sourceId + ".csv";
        String insertSql = "INSERT INTO cfg_extract_process (processId, dataSourceId, targetPath, createdAt, updatedAt) VALUES (?, ?, ?, NOW(), NOW())";

        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, extractProcessDefId); // ID của định nghĩa process (ví dụ: 1 - Extract Northern)
            ps.setInt(2, sourceId);
            ps.setString(3, targetPath);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                recordId = rs.getInt(1);
            }
        }
        return recordId;
    }
}
