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
import java.io.InputStreamReader;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadConfigOperator {
    private String configPath = "/config/configLoader.jar";
    private String logMessage = "";
    private ControlService service = new ControlService();

    public DatabaseConnection loadConfig() {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", configPath);
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

            DatabaseConnection result = extractJsonResultToDatabaseConnection(jsonResult, StorageType.STAGING);
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

    private DatabaseConnection extractJsonResultToDatabaseConnection(JsonObject json, StorageType storageType) {
        try {
            String databaseType = storageType == StorageType.STAGING ? "staging" : "warehouse";
            JsonObject jsonInput = json.getAsJsonObject("database_" + databaseType);

            String name = jsonInput.get("name").getAsString();
            String type = jsonInput.get("type").getAsString();
            String host = jsonInput.get("host").getAsString();
            String port = jsonInput.get("port").getAsString();
            String username = jsonInput.get("username").getAsString();
            String password = jsonInput.get("password").getAsString();
            String characterEncoding = jsonInput.get("characterEncoding").getAsString();

            DatabaseConnection databaseConnection;
            databaseConnection = new DatabaseConnection();
            databaseConnection.setName(name);
            databaseConnection.setType(type);
            databaseConnection.setHost(host);
            databaseConnection.setPort(port);
            databaseConnection.setUsername(username);
            databaseConnection.setPassword(password);
            databaseConnection.setCharacterEncoding(characterEncoding);
            return databaseConnection;
        } catch (Exception e) {
            return null;
        }
    }
}
