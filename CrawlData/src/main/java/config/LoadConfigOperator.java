package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enums.CrawlType;
import enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.DataSource;
import models.ProcessDetail;

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
    private String configControl = "/dw_t4c2n10/config/config.json";
    private String configPath = "/dw_t4c2n10/config/LoadConfig.jar";
    private String logMessage = "";
    private String dataSourceType;
    private DataSource dataSource;

    public DatabaseConnection loadDatabaseConfig() {
        try {
            File configFile = new File(configControl);
            if (!configFile.exists()) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
            br.close();
            JsonObject configJson = JsonParser.parseString(jsonContent.toString()).getAsJsonObject();

            String name = configJson.get("name").getAsString();
            String type = configJson.get("type").getAsString();
            String host = configJson.get("host").getAsString();
            String port = configJson.get("port").getAsString();
            String username = configJson.get("username").getAsString();
            String password = configJson.get("password").getAsString();
            String options = configJson.get("options").getAsString();

            DatabaseConnection databaseConnection;
            databaseConnection = new DatabaseConnection();
            databaseConnection.setName(name);
            databaseConnection.setType(type);
            databaseConnection.setHost(host);
            databaseConnection.setPort(port);
            databaseConnection.setUsername(username);
            databaseConnection.setPassword(password);
            databaseConnection.setOptions(options);
            return databaseConnection;
        } catch (Exception e) {
            return null;
        }
    }

    public void loadConfig(String dataSourceType) {
        this.dataSourceType = dataSourceType;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", configPath, "0", "0", "0", "0", configControl);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            StringBuilder commandResult = new StringBuilder();
            //Read line for output jar result
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                commandResult.append(line);
            }
            br.close();
            //Get exit code
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                //Log err here
            }
            //Parse JSON
            JsonObject jsonResult = JsonParser.parseString(commandResult.toString()).getAsJsonObject();
            this.dataSource = configDataSource(jsonResult);
            configProcess(jsonResult);
        } catch (Exception e) {
            //Log err here
        }
    }

    private void configProcess(JsonObject configJson) {
        ProcessDetail processDetail = ProcessDetail.getInstance();
        JsonArray listProcess = configJson.get("processes").getAsJsonArray();
        String name;
        String targetPath;

        if (this.dataSourceType != null) {
            switch (this.dataSourceType.toUpperCase()) {
                case "NORTH" -> processDetail.updateProcessId(CrawlType.NORTH.getValue());
                case "SOUTH" -> processDetail.updateProcessId(CrawlType.SOUTH.getValue());
                case "MIDDLE" -> processDetail.updateProcessId(CrawlType.MIDDLE.getValue());
                default -> {}
            }
        }

        for (JsonElement process : listProcess) {
            JsonObject obj = process.getAsJsonObject();
            int id = obj.get("id").getAsInt();

            if (id == processDetail.getProcessId()) {
                name = obj.get("name").getAsString();
                targetPath = obj.get("targetPath").getAsString();
                processDetail.initData(name, targetPath);
                break;
            }
        }
        //Log here
        return;
    }

    private DataSource configDataSource(JsonObject jsonResult) {
        JsonArray srcJson = jsonResult.getAsJsonArray("source");
        for (JsonElement source : srcJson) {
            JsonObject sourceJson = source.getAsJsonObject();
            String type = this.dataSourceType;
            CrawlType crawlType;
            switch (type.toUpperCase()) {
                case "NORTH" -> crawlType = CrawlType.NORTH;
                case "SOUTH" -> crawlType = CrawlType.SOUTH;
                case "MIDDLE" -> crawlType = CrawlType.MIDDLE;
                default -> crawlType = null;
            }
            if (crawlType == null) continue;

            int sourceTypeId = crawlType.getValue();
            int sourceId = sourceJson.get("id").getAsInt();
            if (sourceId != sourceTypeId) continue;

            String sourceName = sourceJson.get("name").getAsString();
            String url = sourceJson.get("url").getAsString();

            DataSource result = new DataSource();
            result.setSourceId(sourceId);
            result.setName(sourceName);
            result.setUrl(url);
            result.setType(crawlType);
            return result;
        }
        return null;
    }
}