package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.CrawlResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfigLoader {
    private String configPath;

    public DatabaseConnection loadDatabaseConfig() {
        try {
            File configFile = new File(configPath);
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            //Skip header
            br.readLine();
            String line = "";
            DatabaseConnection databaseConnection = null;
            while ((line = br.readLine()) != null && databaseConnection == null) {
                //Temp, will replaced by library here
                String[] parsedLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                //Define prop later
                String name = parsedLine[0];
                String type = parsedLine[1];
                String host = parsedLine[2];
                String port = parsedLine[3];
                String username = parsedLine[4];
                String password = parsedLine[5];
                String characterEncoding = parsedLine[6];

                databaseConnection = new DatabaseConnection();
                databaseConnection.setName(name);
                databaseConnection.setType(type);
                databaseConnection.setHost(host);
                databaseConnection.setPort(port);
                databaseConnection.setUsername(username);
                databaseConnection.setPassword(password);
                databaseConnection.setCharacterEncoding(characterEncoding);
            }
            br.close();
            return databaseConnection;
        }
        catch (IOException e) {
            return null;
        }
    }
}
