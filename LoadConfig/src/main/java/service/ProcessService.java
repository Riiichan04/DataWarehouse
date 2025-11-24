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

                targetPath = switch (id) {
                    // Extract (1, 2, 3)
                    case 1, 2, 3 -> "/dw_t4c2n10/staging/result/result_" + id + "_" + runDate + ".csv";

                    // Load Staging (4)
                    case 4 -> "/dw_t4c2n10/staging/result";

                    // Transform Fact (5)
                    case 5 -> "/dw_t4c2n10/staging/transform_warehouse.json";

                    // Transform Dim (6)
                    case 6 -> "/dw_t4c2n10/staging/dim_warehouse.json";

                    // Load Warehouse (7, 8)
                    case 7, 8 -> "/dw_t4c2n10/warehouse/";

                    // Load Presentation / Dim Staging (9)
                    case 9 -> "/dw_t4c2n10/presentation/";

                    // Load aggerate (10)
                    case 10 -> "";

                    //Dump aggerate (11)
                    //Load to Data Mart (12)
                    case 11, 12 -> "/dw_t4c2n10/aggregate/aggregate_" + runDate + ".json";
                    default -> "";
                };


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
