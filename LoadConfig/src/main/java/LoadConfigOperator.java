
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.DbUtil;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public class LoadConfigOperator {


    public static void main(String[] args) {
        try {
            if (args.length < 5) {
                System.err.println("Usage: java -jar LoadConfig.jar <days> <hours> <minutes> <seconds> <config_path>");
                System.exit(1);
            }


            // ====== 1. Parse Offset Parameters ======
            long offsetDays = Long.parseLong(args[0]);
            long offsetHours = Long.parseLong(args[1]);
            long offsetMinutes = Long.parseLong(args[2]);
            long offsetSeconds = Long.parseLong(args[3]);

            String baseConfigPath = args[4];

            // ====== 2. Load base config.json ======
            System.out.println("Reading config: " + baseConfigPath);


            JSONObject dbCfg = loadJsonFromFile(baseConfigPath);
            String host = dbCfg.get("host").toString();
            String port = dbCfg.get("port").toString();
            String dbname = dbCfg.get("dbname").toString();
            String username = dbCfg.get("username").toString();
            String password = dbCfg.get("password").toString();

            String url = "jdbc:mysql://" +
                    host + ":" +
                    port + "/" +
                    dbname;
            int sourceId=-1;
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                List<JSONObject> sourceList = new ArrayList<>();

                try (PreparedStatement psSource = conn.prepareStatement(
                        "SELECT * FROM cfg_source WHERE isactive = 1"
                );
                     ResultSet rsSource = psSource.executeQuery()) {

                    boolean hasData = false;

                    while (rsSource.next()) {
                        hasData = true;
                        JSONObject obj = new JSONObject();

                        obj.put("id", rsSource.getInt("id"));
                        obj.put("name", rsSource.getString("name"));
                        obj.put("url", rsSource.getString("url"));
                        sourceList.add(obj);
                        sourceId = rsSource.getInt("id");
                    }

                    if (!hasData) {
                        System.err.println("No active source!");
                        return;
                    }
                }

                LocalDateTime now = LocalDateTime.now()
                        .plusDays(offsetDays)
                        .plusHours(offsetHours)
                        .plusMinutes(offsetMinutes)
                        .plusSeconds(offsetSeconds);

                String runDate = now.toLocalDate().toString();
                JSONArray arrSourceProcess = new JSONArray();
                JSONArray arrStagingProcess = new JSONArray();
                JSONArray arrWarehouseProcess = new JSONArray();
                for (JSONObject sourceObj : sourceList) {

                    int sid = sourceObj.getInt("id");

                    JSONArray sp = ensureDailySourceProcess(conn, sid, runDate);
                    arrSourceProcess.put(sp.getJSONObject(0));

                    JSONArray st = ensureDailyStagingProcess(conn, sp.getJSONObject(0).getInt("id"), runDate);
                    arrStagingProcess.put(st.getJSONObject(0));

                    JSONArray wp = ensureDailyWarehouseProcess(conn, st.getJSONObject(0).getInt("id"), runDate);
                    arrWarehouseProcess.put(wp.getJSONObject(0));
                }

                String runtimeConfigPath = "/dw_t4c2n10/staging/runtime_config.json";


                JSONObject runtimeJSON = new JSONObject();
                runtimeJSON.put("runDate", runDate);
                runtimeJSON.put("source", new JSONArray(sourceList));
                runtimeJSON.put("source_process", arrSourceProcess);
                runtimeJSON.put("staging_process", arrStagingProcess);
                runtimeJSON.put("warehouse_process", arrWarehouseProcess);

                JSONObject scripts = new JSONObject();
                scripts.put("dataCrawler", "/dw_t4c2n10/staging/dataCrawler.jar");
                scripts.put("dataTransformer", "/dw_t4c2n10/staging/dataTransformer.jar");
                scripts.put("stagingLoader", "/dw_t4c2n10/staging//stagingLoader.jar");
                scripts.put("warehouseTransformer", "/dw_t4c2n10/staging/warehouseTransformer.jar");
                scripts.put("warehouseLoader", "/dw_t4c2n10/staging/warehouseLoader.jar");

                runtimeJSON.put("scripts", scripts);
                Files.createDirectories(Paths.get(runtimeConfigPath).getParent());
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(runtimeConfigPath))) {
                    writer.write(runtimeJSON.toString(4));
                }
                System.out.println("runtime_config.json created at: " + runtimeConfigPath);

//                runJar(runtimeJSON.getJSONObject("scripts").getString("dataCrawler"), runtimeConfigPath);
//                runJar(runtimeJSON.getJSONObject("scripts").getString("dataTransformer"), runtimeConfigPath);
//                runJar(runtimeJSON.getJSONObject("scripts").getString("stagingLoader"), runtimeConfigPath);
//                runJar(runtimeJSON.getJSONObject("scripts").getString("warehouseTransformer"), runtimeConfigPath);
//                runJar(runtimeJSON.getJSONObject("scripts").getString("warehouseLoader"), runtimeConfigPath);


                System.out.println("All scripts completed successfully.");

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static JSONObject loadJsonFromFile(String configPath) throws IOException {
        JSONObject json = null;
        try (FileReader reader = new FileReader(configPath)) {
            json = new JSONObject(new JSONTokener(reader));
        }
        return json;
    }

    static JSONArray ensureDailySourceProcess(Connection conn, int sourceId, String runDate) throws SQLException {
        JSONArray arrSourceProcess = new JSONArray();

        String sql = "SELECT * FROM cfg_source_process WHERE dataSourceId=? AND name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sourceId);
            ps.setString(2, "%_" + runDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("name", rs.getString("name"));
                obj.put("targetPath", rs.getString("targetPath"));
                arrSourceProcess.put(obj);
                return arrSourceProcess;
            }
        }

        String name = "crawl_" + runDate;
        String targetPath = "/dw_t4c2n10/staging/result/result_" + runDate + ".csv";
        String insertSQL = "INSERT INTO cfg_source_process (name, description, dataSourceId, targetPath, dependentProcessId, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement psInsert = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setString(1, name);
            psInsert.setString(2, "Daily crawl process " + runDate);
            psInsert.setInt(3, sourceId);
            psInsert.setString(4, targetPath);
            psInsert.setObject(5, null); // dependentProcessId = null
            psInsert.executeUpdate();
            ResultSet generatedKeys = psInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", generatedKeys.getInt(1));
                obj.put("name", name);
                obj.put("targetPath", targetPath);
                arrSourceProcess.put(obj);
            } else {
                throw new SQLException("Creating source process failed, no ID obtained.");
            }
        }
        return arrSourceProcess;
    }


    static JSONArray ensureDailyStagingProcess(Connection conn, int sourceProcessId, String runDate) throws SQLException {
        JSONArray arrStagingProcess = new JSONArray();

        String selectSql = "SELECT * FROM cfg_staging_process WHERE dependentProcessId=? AND name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, sourceProcessId);
            ps.setString(2, "%_" + runDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("name", rs.getString("name"));
                obj.put("sourcePath", rs.getString("sourcePath"));
                obj.put("targetTableName", rs.getString("targetTableName"));
                arrStagingProcess.put(obj);
            }
            if (arrStagingProcess.length() > 0) return arrStagingProcess;
        }

        String name = "staging_" + runDate;
        String sourcePath = "/dw_t4c2n10/staging/result/result_" + runDate + ".csv";
        String targetTable = "lottery_staging"; // hoặc lấy từ config cơ bản
        String insertSql = "INSERT INTO cfg_staging_process (name, description, sourcePath, targetDbId, targetTableName, dependentProcessId, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, 1, ?, ?, NOW(), NOW())";

        try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setString(1, name);
            psInsert.setString(2, "Daily staging process " + runDate);
            psInsert.setString(3, sourcePath);
            psInsert.setString(4, targetTable);
            psInsert.setInt(5, sourceProcessId);
            psInsert.executeUpdate();
            ResultSet generatedKeys = psInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", generatedKeys.getInt(1));
                obj.put("name", name);
                obj.put("sourcePath", sourcePath);
                obj.put("targetTableName", targetTable);
                arrStagingProcess.put(obj);
            }
        }

        return arrStagingProcess;

    }

    static JSONArray ensureDailyWarehouseProcess(Connection conn, int stagingProcessId, String runDate) throws SQLException {
        JSONArray arrWarehouseProcess = new JSONArray();


        String selectSql = "SELECT * FROM cfg_warehouse_process WHERE dependentProcessId=? AND name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, stagingProcessId);
            ps.setString(2, "%_" + runDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("name", rs.getString("name"));
                obj.put("sourceTableName", rs.getString("sourceTableName"));
                obj.put("targetTableName", rs.getString("targetTableName"));
                arrWarehouseProcess.put(obj);
            }
            if (arrWarehouseProcess.length() > 0) return arrWarehouseProcess;
        }

        String name = "warehouse_" + runDate;
        String sourceTable = "lottery_staging";
        String targetTable = "fact_prize_results";
        String insertSql = "INSERT INTO cfg_warehouse_process (name, description, sourceDbId, sourceTableName, targetDbId, targetTableName, dependentProcessId, createdAt, updatedAt) " +
                "VALUES (?, ?, 1, ?, 2, ?, ?, NOW(), NOW())";

        try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setString(1, name);
            psInsert.setString(2, "Daily warehouse process " + runDate);
            psInsert.setString(3, sourceTable);
            psInsert.setString(4, targetTable);
            psInsert.setInt(5, stagingProcessId);
            psInsert.executeUpdate();
            ResultSet generatedKeys = psInsert.getGeneratedKeys();
            if (generatedKeys.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", generatedKeys.getInt(1));
                obj.put("name", name);
                obj.put("sourceTableName", sourceTable);
                obj.put("targetTableName", targetTable);
                arrWarehouseProcess.put(obj);
            }
        }

        return arrWarehouseProcess;

    }


    private static void runJar(String jarPath, String runtimeConfigPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", jarPath, runtimeConfigPath
        );
        pb.redirectErrorStream(true);
        Process proc = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[JAR] " + line);
            }
        }

        int exitCode = proc.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("JAR failed: " + jarPath + " Exit code: " + exitCode);
        }
    }

}
