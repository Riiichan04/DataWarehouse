package services;

import com.google.gson.JsonObject;
import enums.MartLoadStatus;
import models.MartLoadSummary;
import models.MartTableLoadDetail;

import java.util.ArrayList;
import java.util.List;

public class MartControlService {

    private final MartLoadService loadService = new MartLoadService();

    public MartLoadSummary run(JsonObject aggregateJson) {

        List<MartTableLoadDetail> details = new ArrayList<>();

        int successCount = 0;
        int failedCount = 0;

        for (String tableName : aggregateJson.keySet()) {
            try {
                int rowCount = loadService.loadTable(tableName, aggregateJson.getAsJsonArray(tableName));

                details.add(new MartTableLoadDetail(
                        tableName,
                        rowCount,
                        MartLoadStatus.SUCCESS,
                        "Loaded successfully"
                ));

                successCount++;
            } catch (Exception e) {
                details.add(new MartTableLoadDetail(
                        tableName,
                        0,
                        MartLoadStatus.FAILED,
                        e.getMessage()
                ));
                failedCount++;
            }
        }

        return new MartLoadSummary(
                failedCount == 0,
                aggregateJson.keySet().size(),
                successCount,
                failedCount,
                details
        );
    }
}
