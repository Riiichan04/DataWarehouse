package config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.DataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoadConfigOperator {
    private String configPath = "/config/configLoader.jar";

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

            return new DataSource(sourceId, sourceName, url, targetPath);
        }
        catch (Exception e) {
            //Log err here
            return null;
        }
    }
}