package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ConfigLoader {
    // Định nghĩa loại DB dựa trên giá trị trong file cấu hình JSON
    private static final int DB_WAREHOUSE_TYPE = 2;
    private static final String configPath = "/dw_t4c2n10/config/LoadConfig.jar"; // Đường dẫn tới file JAR cấu hình
    private String configJsonPath = "/dw_t4c2n10/config/config.json";

    private JsonObject loadJsonFromJar() {
        try {
            // Khởi chạy file JAR bên ngoài
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", configPath, "0", "0", "0", "0", configJsonPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder commandResult = new StringBuilder();
            // Đọc output từ file JAR
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                commandResult.append(line);
            }

            // Chờ quá trình kết thúc và kiểm tra mã thoát
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Error when running loadConfig file. Exit code: " + exitCode);
                return null;
            }

            // Phân tích chuỗi JSON nhận được
            return JsonParser.parseString(commandResult.toString()).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("Exception occurred while loading config from jar: " + e.getMessage());
            return null;
        }
    }

    public DatabaseConnection loadControlDBConfig() {
        File file = new File(configJsonPath);
        if (!file.exists()) {
            System.out.println("Config file not found.");
            return null;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
            br.close();
            JsonObject jsonInput = JsonParser.parseString(jsonContent.toString()).getAsJsonObject();

            return createDatabaseConnection(jsonInput);
        }
        catch (Exception e) {
            return null;
        }
    }

    public DatabaseConnection loadWarehouseDBConfig() {
        JsonObject jsonResult = loadJsonFromJar();
        if (jsonResult == null) {
            return null;
        }
        return extractJsonResultToDatabaseConnection(jsonResult, DB_WAREHOUSE_TYPE);
    }

    private DatabaseConnection extractJsonResultToDatabaseConnection(JsonObject json, int type) {
        try {
            // Lấy mảng "db"
            JsonArray listDb = json.getAsJsonArray("db");

            // Duyệt qua từng kết nối DB trong mảng
            for (JsonElement db : listDb) {
                JsonObject jsonInput = db.getAsJsonObject();
                int dbType = jsonInput.get("type").getAsInt();

                // So sánh loại DB
                if (dbType == type) {
                    return createDatabaseConnection(jsonInput);
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error during JSON extraction for type " + type + ": " + e.getMessage());
            return null;
        }
    }

    private DatabaseConnection createDatabaseConnection(JsonObject jsonInput) {
        // Trích xuất các trường dữ liệu
        String name = jsonInput.get("name").getAsString();
        String type = jsonInput.get("type").getAsString();
        String host = jsonInput.get("host").getAsString();
        String port = jsonInput.get("port").getAsString();
        String username = jsonInput.get("username").getAsString();
        String password = jsonInput.get("password").getAsString();

        DatabaseConnection databaseConnection = new DatabaseConnection();
        databaseConnection.setName(name);
        databaseConnection.setType(type);
        databaseConnection.setHost(host);
        databaseConnection.setPort(port);
        databaseConnection.setUsername(username);
        databaseConnection.setPassword(password);
        return databaseConnection;
    }
}