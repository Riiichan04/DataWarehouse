package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enums.LogLevel;
import enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.ProcessDetail;
import services.ControlService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadConfigOperator {
    private String configPath = "/dw_t4c2n10/config/LoadConfig.jar";
    private String configJsonPath = "/dw_t4c2n10/config/config.json";
    private String logMessage = "";

    public DatabaseConnection loadWarehouseDatabase() {
        ControlService service = new ControlService();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", configPath,  "0", "0", "0", "0", configJsonPath);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder commandResult = new StringBuilder();
            //Read line for output jar result
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                commandResult.append(line);
            }
            //Get exit code
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                //Log err here
                System.out.println("Error when running loadConfig file");
                return null;
            }

            //Parse JSON
            JsonObject jsonResult = JsonParser.parseString(commandResult.toString()).getAsJsonObject();
            //Load process detail
            ProcessDetail processDetail = ProcessDetail.getInstance();
            JsonArray listJsonProcess = jsonResult.getAsJsonArray("processes");
            JsonObject jsonProcess = null;
            for (JsonElement json : listJsonProcess.asList()) {
                JsonObject obj = json.getAsJsonObject();
                if (obj.get("id").getAsInt() == processDetail.getProcessId()) {
                    jsonProcess = obj;
                }
            }

            if (jsonProcess == null) {
                //Log failure here
                logMessage = "Process not found.";
                service.addNewLog(
                        processDetail.getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.ERROR.getLevel(),
                        logMessage
                );
                return null;
            }

            String processName = jsonProcess.get("name").getAsString();
            String processTarget = jsonProcess.get("targetPath").getAsString();
            processDetail.initData(processName, processTarget);
            DatabaseConnection result = extractJsonResultToDatabaseConnection(jsonResult, StorageType.DATA_MART);
            //If result != null
            if (result != null) {
                //Log success here
                logMessage = "Extracting data success";
                service.addNewLog(
                        processDetail.getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.SUCCESS.getLevel(),
                        logMessage
                );
            } else {
                logMessage = "Error when extracting data from process.";
                service.addNewLog(
                        processDetail.getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.ERROR.getLevel(),
                        logMessage
                );
            } //Log failure here
            return result;
        } catch (Exception e) {
            //Log err here
            logMessage = "Error when extracting data from process. Error detail: " + e.getMessage();
            service.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    logMessage
            );
            return null;
        }
    }

    public DatabaseConnection loadControlDatabase() {
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

    private DatabaseConnection extractJsonResultToDatabaseConnection(JsonObject json, StorageType storageType) {
        try {
            JsonArray listDb = json.getAsJsonArray("db");
            for (JsonElement db : listDb) {
                JsonObject jsonInput = db.getAsJsonObject();
                int dbType = jsonInput.get("type").getAsInt();
                if (dbType == storageType.getId()) {
                    return createDatabaseConnection(jsonInput);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private DatabaseConnection createDatabaseConnection(JsonObject jsonInput) {
        String name = jsonInput.get("name").getAsString();
        String type = jsonInput.get("type").getAsString();
        String host = jsonInput.get("host").getAsString();
        String port = jsonInput.get("port").getAsString();
        String username = jsonInput.get("username").getAsString();
        String password = jsonInput.get("password").getAsString();

        DatabaseConnection databaseConnection;
        databaseConnection = new DatabaseConnection();
        databaseConnection.setName(name);
        databaseConnection.setType(type);
        databaseConnection.setHost(host);
        databaseConnection.setPort(port);
        databaseConnection.setUsername(username);
        databaseConnection.setPassword(password);
        databaseConnection.setOptions("useUnicode=true&characterEncoding=UTF-8");
        return databaseConnection;
    }
}