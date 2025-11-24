package service;

import model.Process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcessService {

    public List<Process> getAllProcesses(Connection conn, String runDate) throws SQLException {
        List<Process> list = new ArrayList<>();
        String sql = "SELECT id, name, description, scriptName, typeProcess FROM process ORDER BY id";
        boolean isExtractAdded = false;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id =  rs.getInt("id");
                String type = rs.getString("typeProcess");
                String targetPath = "";
                String name = rs.getString("name");
                String desc = rs.getString("description");

                if("extract".equals(type)) {
                    if(isExtractAdded) {
                        continue;
                    }

                    isExtractAdded = true;

                    name = "Extract (all regions)";
                    desc = "Extract all regions";
                }

                switch (id) {
                    // Extract (1, 2, 3)
                    case 1:
                        targetPath = "/dw_t4c2n10/staging/result/";
                    break;

                    // Load Staging (4)
                    case 4:
                        targetPath = "/dw_t4c2n10/staging/result/result_" + runDate + ".csv";
                        break;

                    // Transform Fact (5)
                    case 5:
                        targetPath = "/dw_t4c2n10/staging/transform_warehouse.json";
                        break;

                    // Transform Dim (6)
                    case 6:
                        targetPath = "/dw_t4c2n10/staging/dim_warehouse.json";
                        break;

                    // Load Warehouse (7, 8)
                    case 7:
                    case 8:
                        targetPath = "/dw_t4c2n10/warehouse/";
                        break;

                    // Load Presentation / Dim Staging (9)
                    case 9:
                        targetPath = "/dw_t4c2n10/presentation/";
                        break;

                    default:
                        targetPath = "";
                        break;
                }


                list.add(new Process(
                        rs.getInt("id"),
                        name,
                        desc,
                        rs.getString("scriptName"),
                        type,
                        targetPath
                ));
            }
        }
        return list;
    }
}
