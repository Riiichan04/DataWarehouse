import model.DatabaseConfig;
import model.Process;
import model.SourceConfig;
import org.json.JSONObject;
import org.json.JSONTokener;
import service.DatabaseService;
import service.ProcessService;
import service.SourceService;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

public class LoadConfigOperator {

    private static final String JAR_BASE_DIR = "/dw_t4c2n10/staging/";

    public static void main(String[] args) {
        try {
            if (args.length < 5) {
                System.err.println("Usage: java -jar LoadConfig.jar <d> <h> <m> <s> <path>");
                System.exit(1);
            }

            String configPath = args[4];
            System.out.println("Loading config from: " + configPath);
            JSONObject dbCfg = loadJsonFromFile(configPath);

            String runDate = calculateRunDate(args);

            String url = "jdbc:mysql://" + dbCfg.get("host") + ":" + dbCfg.get("port") + "/" + dbCfg.get("dbname");
            try (Connection conn = DriverManager.getConnection(url, dbCfg.getString("username"), dbCfg.getString("password"))) {

                SourceService sourceService = new SourceService();
                DatabaseService dbService = new DatabaseService();
                ProcessService processService = new ProcessService();

                List<SourceConfig> sourceConfigs = sourceService.getListActiveSources(conn);
                List<DatabaseConfig> databases = dbService.getActiveDatabases(conn);
                List<Process> processes = processService.getAllProcesses(conn,runDate);

                JSONObject runtimeJSON = new JSONObject();
                runtimeJSON.put("runDate", runDate);

                runtimeJSON.put("source", sourceConfigs);
                runtimeJSON.put("db", databases);
                runtimeJSON.put("processes", processes);

                // OUTPUT
                System.out.println(runtimeJSON.toString(4));

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String calculateRunDate(String[] args) {
        long d = Long.parseLong(args[0]);
        long h = Long.parseLong(args[1]);
        long m = Long.parseLong(args[2]);
        long s = Long.parseLong(args[3]);
        return LocalDateTime.now().plusDays(d).plusHours(h).plusMinutes(m).plusSeconds(s)
                .toLocalDate().toString();
    }

    private static JSONObject loadJsonFromFile(String path) throws IOException {
        try (FileReader reader = new FileReader(path)) {
            return new JSONObject(new JSONTokener(reader));
        }
    }
}