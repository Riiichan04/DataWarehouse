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
    private String configControl = "/config/config.json";
    private String configPath = "/config/configLoader.jar";
    private String logMessage = "";
    private ControlService service = new ControlService();

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
            String characterEncoding = configJson.get("characterEncoding").getAsString();

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

    public DataSource loadConfig() {
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
            }

            //Parse JSON
            JsonObject jsonResult = JsonParser.parseString(commandResult.toString()).getAsJsonObject();
            JsonObject sourceJson = jsonResult.getAsJsonObject("source");
            JsonObject processJson = jsonResult.getAsJsonObject("source_process");

            int sourceId = sourceJson.get("id").getAsInt();
            String sourceName = sourceJson.get("name").getAsString();
            String url = sourceJson.get("url").getAsString();
            String targetPath = processJson.get("targetPath").getAsString();

            String type = jsonResult.get("type").getAsString();
            CrawlType crawlType;
            switch (type.toUpperCase()) {
                case "NORTH" -> crawlType = CrawlType.NORTH;
                case "SOUTH" -> crawlType = CrawlType.SOUTH;
                case "MIDDLE" -> crawlType = CrawlType.MIDDLE;
                default -> crawlType = null;
            }

            if (crawlType == null) return null;

            DataSource result = new DataSource();
            result.setSourceId(sourceId);
            result.setName(sourceName);
            result.setUrl(url);
            result.setTargetPath(targetPath);
            result.setType(crawlType);
            return result;
        }
        catch (Exception e) {
            //Log err here
            return null;
        }
    }
}