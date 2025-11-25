import config.WarehouseConnector;
import org.jdbi.v3.core.Jdbi;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AggregateDump {


    public static void main(String[] args) {

        String[] tables = {
                "agg_prize_result_freq",
                "agg_number_week_result_freq",
                "agg_tail_prize_freq",
                "agg_region_result_freq",
                "dim_region"
        };


        JSONObject allData = new JSONObject();
        for (String table : tables) {
            JSONArray data = fetchTableData(table);
            allData.put(table, data);
        }

        String dateStr = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String basePath = "dw_t4c2n10" + File.separator + "staging" + File.separator + "aggregate";
        File folder = new File(basePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String outputFile = basePath + File.separator + "aggregate_" + dateStr + ".json";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            bw.write(allData.toString(4));
            System.out.println("Exported all tables to JSON: " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static JSONArray fetchTableData(String tableName) {
        JSONArray tableData = new JSONArray();
        Jdbi jdbi = WarehouseConnector.getInstance();

        jdbi.useHandle(handle -> {
            handle.createQuery("SELECT * FROM " + tableName)
                    .map((rs, ctx) -> {
                        JSONObject row = new JSONObject();
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnLabel(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value != null ? value : JSONObject.NULL);
                        }
                        return row;
                    })
                    .forEach(tableData::put);
        });

        System.out.println("Fetched " + tableData.length() + " rows from table: " + tableName);
        return tableData;
    }

    }
