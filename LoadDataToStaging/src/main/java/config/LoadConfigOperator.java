package config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enums.StorageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadConfigOperator {
    private String configPath = "/config/configLoader.jar";

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
            }

            //Parse JSON
            JsonObject jsonResult = JsonParser.parseString(commandResult.toString()).getAsJsonObject();
            //Log sucess here
            return extractJsonResultToDatabaseConnection(jsonResult, StorageType.STAGING);
        }
        catch (Exception e) {
            //Log err here
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

            DatabaseConnection databaseConnection = null;
            databaseConnection = new DatabaseConnection();
            databaseConnection.setName(name);
            databaseConnection.setType(type);
            databaseConnection.setHost(host);
            databaseConnection.setPort(port);
            databaseConnection.setUsername(username);
            databaseConnection.setPassword(password);
            databaseConnection.setCharacterEncoding(characterEncoding);
            return databaseConnection;
        }
        catch (Exception e) {
            return null;
        }
    }
}
