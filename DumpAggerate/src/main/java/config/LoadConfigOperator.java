package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import enums.LogLevel;
import enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.ProcessDetail;
import services.ControlService;
import util.AESUtil;

import java.io.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadConfigOperator {
//    private String configPath = "/config/configLoader.jar";
//    private String configJsonPath = "/config/config.json";

    private String configPath = "F:/Github/DataWarehouse/DumpAggerate/src/main/resources/config/LoadConfig.jar";
    private String configJsonPath = "F:/Github/DataWarehouse/DumpAggerate/src/main/resources/config/config.json";
    private String logMessage = "";

    public DatabaseConnection loadWarehouseDatabase() {

        try {

            File jarFile = new File(configPath);
            if (!jarFile.exists()) {
                System.err.println("CRITICAL ERROR: configLoader.jar not found at: " + jarFile.getAbsolutePath());
                return null;
            }

            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", configPath,"0","0","0","0", configJsonPath);
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
                System.out.println("================ ERROR LOG ================");
                System.out.println("Exit Code: " + exitCode);
                System.out.println("JAR Output: \n" + commandResult.toString());
                System.out.println("===========================================");
                return null;
            }
            System.out.println("JAR Output: \n" + commandResult.toString());


            String output = commandResult.toString();

            int jsonStart = output.indexOf("{");
            if (jsonStart < 0) {
                System.err.println("No JSON found in JAR output");
                return null;
            }

            String jsonString = output.substring(jsonStart);

            JsonObject jsonResult = JsonParser.parseString(jsonString).getAsJsonObject();

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
                ControlConnector.initialize(loadControlDatabase());
                ControlService service = new ControlService();
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

            DatabaseConnection result = extractJsonResultToDatabaseConnection(jsonResult, StorageType.WAREHOUSE);
            //If result != null

            if (result == null) {
                writeErrorLog("Warehouse DB info not found");
                return null;
            }

            writeSuccessLog("Extract warehouse DB success");

            return result;
        } catch (Exception e) {
            writeErrorLog("Exception: " + e.getMessage());
            return null;
        }
    }
    private void writeErrorLog(String msg) {
        DatabaseConnection controlDb = loadControlDatabase();
        if (controlDb != null) {
            ControlConnector.initialize(controlDb);
            new ControlService().addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    now(),
                    now(),
                    LogLevel.ERROR.getLevel(),
                    msg
            );
        }
    }

    private void writeSuccessLog(String msg) {
        DatabaseConnection controlDb = loadControlDatabase();
        if (controlDb != null) {
            ControlConnector.initialize(controlDb);
            new ControlService().addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    now(),
                    now(),
                    LogLevel.SUCCESS.getLevel(),
                    msg
            );
        }
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
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
                if (dbType == 2) {
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
