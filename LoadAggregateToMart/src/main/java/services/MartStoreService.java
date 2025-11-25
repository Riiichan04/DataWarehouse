package services;

import models.MartLoadSummary;
import models.MartTableLoadDetail;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class MartStoreService {

    public void saveResult(MartLoadSummary summary) {
        try (FileWriter fw = new FileWriter("result/load_aggregate_to_mart.log", true)) {

            fw.write("==== MART LOAD RESULT ====\n");
            fw.write("Time: " + LocalDateTime.now() + "\n");
            fw.write("Success: " + summary.isSuccess() + "\n");
            fw.write("Tables: " + summary.getTotalTables() + "\n");
            fw.write("SuccessTables: " + summary.getSuccessTables() + "\n");
            fw.write("FailedTables: " + summary.getFailedTables() + "\n\n");

            for (MartTableLoadDetail d : summary.getDetails()) {
                fw.write("- " + d.getTableName() +
                        " | rows=" + d.getRowCount() +
                        " | status=" + d.getStatus() +
                        " | message=" + d.getMessage() + "\n");
            }

            fw.write("\n=========================\n");

        } catch (Exception e) {
            System.err.println("Error writing result log: " + e.getMessage());
        }
    }
}

