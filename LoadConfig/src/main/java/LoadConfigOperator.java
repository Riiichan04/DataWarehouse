import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.Config;
import service.ProcessService;
import service.SourceService;
import util.DbUtil;

import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

//FIXME: This is a temp file for defining structure

//3.4 Viết Script loadConfig từ db.config
public class LoadConfigOperator {

    public static void main(String[] args) {

        try {
            String runDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));

            Properties prop = DbUtil.loadProperties();
            Connection conn = DbUtil.getConnection(prop);

            SourceService sourceRepo = new SourceService();
            ProcessService processRepo = new ProcessService();

            // 1. Lấy source
            var source = sourceRepo.getActiveSource(conn);
            if (source == null) {
                System.out.println("No active source found!");
                return;
            }

            // 2. Tạo source_process nếu chưa có
            String targetCsv = prop.getProperty("staging.result.folder") + "/result_" + runDate + ".csv";
            sourceRepo.createSourceProcessIfNotExist(conn, source.getSourceId(), targetCsv, runDate);

            // 3. Lấy danh sách process
            var processList = processRepo.getProcessList(conn, source.getSourceId());

            // 4. Xuất config.json
            Config config = new Config();
            config.setRunDate(runDate);
            config.setSource(source);
            config.setProcessList(processList);

            String configJsonPath = prop.getProperty("config.json.path");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            try (FileWriter writer = new FileWriter(configJsonPath)) {
                gson.toJson(config, writer);
                System.out.println("config.json created at " + configJsonPath);
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
