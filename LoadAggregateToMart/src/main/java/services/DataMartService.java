package services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.WarehouseConnector;
import dao.DataMartDAO;
import enums.LogLevel;
import models.ProcessDetail;

import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;

public class DataMartService {
    DataMartDAO dataMartDAO;
    ControlService controlService;

    public DataMartService() {
        controlService = new ControlService();
        dataMartDAO = WarehouseConnector.getInstance().onDemand(DataMartDAO.class);
    }

    public void loadFromJson(String targetPath) {
        File target = new File(targetPath);
        if (!target.exists()) return;
        try {
            JsonObject jsonObject = JsonParser.parseReader(new FileReader(target.getAbsolutePath())).getAsJsonObject();
            insertMostAppear(jsonObject);
            insertResultFrequencies(jsonObject);
            insertPairFrequencies(jsonObject);
            insertAggRegionResultFrequencies(jsonObject);
            insertRegionDim(jsonObject);

            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.SUCCESS.getLevel(),
                    "Insert aggregate data successful."
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Insert aggregate data failed. Error detail " + e.getMessage()
            );
        }
    }

    private void insertMostAppear(JsonObject jsonObject) {
        JsonArray jsonArr = jsonObject.getAsJsonArray("agg_prize_result_freq");
        dataMartDAO.truncateAggHistoryPrizes();
        for (JsonElement ele : jsonArr) {
            JsonObject inputJson = ele.getAsJsonObject();
            int prizeId = inputJson.get("prizeId").getAsInt();
            String result = inputJson.get("result").getAsString();
            int frequency = inputJson.get("frequency").getAsInt();
            Timestamp timestamp = Timestamp.valueOf(inputJson.get("createdAt").getAsString());

            WarehouseConnector.getInstance().useTransaction(handle -> {
                dataMartDAO = handle.attach(DataMartDAO.class);
                dataMartDAO.insertMostAppearReward(prizeId, result, timestamp, frequency);
            });
        }

    }

    private void insertResultFrequencies(JsonObject jsonObject) {
        JsonArray jsonArr = jsonObject.getAsJsonArray("agg_number_week_result_freq");
        dataMartDAO.truncateAggResultFrequencies();
        for (JsonElement ele : jsonArr) {
            JsonObject inputJson = ele.getAsJsonObject();
            int numberOfWeek = inputJson.get("numberOfWeek").getAsInt();
            int prizeId = inputJson.get("prizeId").getAsInt();
            String result = inputJson.get("result").getAsString();
            int frequency = inputJson.get("frequency").getAsInt();
            Timestamp timestamp = Timestamp.valueOf(inputJson.get("createdAt").getAsString());

            WarehouseConnector.getInstance().useTransaction(handle -> {
                dataMartDAO = handle.attach(DataMartDAO.class);
                dataMartDAO.insertResultFrequencies(result, numberOfWeek, timestamp, prizeId, frequency);
            });
        }
    }

    private void insertPairFrequencies(JsonObject jsonObject) {
        JsonArray jsonArr = jsonObject.getAsJsonArray("agg_tail_prize_freq");
        dataMartDAO.truncateAggPairFrequencies();
        for (JsonElement ele : jsonArr) {
            JsonObject inputJson = ele.getAsJsonObject();
            int prizeId = inputJson.get("prizeId").getAsInt();
            String result = inputJson.get("tailResult").getAsString();
            int frequency = inputJson.get("frequency").getAsInt();
            Timestamp timestamp = Timestamp.valueOf(inputJson.get("createdAt").getAsString());

            WarehouseConnector.getInstance().useTransaction(handle -> {
                dataMartDAO = handle.attach(DataMartDAO.class);
                dataMartDAO.insertPairFrequencies(result, prizeId, timestamp, frequency);
            });
        }

    }

    private void insertAggRegionResultFrequencies(JsonObject jsonObject) {
        JsonArray jsonArr = jsonObject.getAsJsonArray("agg_region_result_freq");
        dataMartDAO.truncateAggRegionResultFrequencies();

        for (JsonElement ele : jsonArr) {
            JsonObject inputJson = ele.getAsJsonObject();
            int regionId = inputJson.get("regionId").getAsInt();
            int prizeId = inputJson.get("prizeId").getAsInt();
            String result = inputJson.get("result").getAsString();
            int frequency = inputJson.get("frequency").getAsInt();
            Timestamp timestamp = Timestamp.valueOf(inputJson.get("createdAt").getAsString());

            WarehouseConnector.getInstance().useTransaction(handle -> {
                dataMartDAO = handle.attach(DataMartDAO.class);
                dataMartDAO.insertRegionResultFrequencies(regionId, prizeId, result, timestamp, frequency);
            });
        }
    }

    public void insertRegionDim(JsonObject jsonObject) {
        JsonArray jsonArr = jsonObject.getAsJsonArray("dim_region");

        dataMartDAO.truncateDimRegion();
        for (JsonElement ele : jsonArr) {
            JsonObject inputJson = ele.getAsJsonObject();
            String codeRegion = inputJson.get("codeRegion").getAsString();
            String name = inputJson.get("name").getAsString();
            String description = inputJson.get("description").getAsString();
            Timestamp createdAt = Timestamp.valueOf(inputJson.get("createdAt").getAsString());
            Timestamp expiredAt = Timestamp.valueOf(inputJson.get("expiredAt").getAsString());
            WarehouseConnector.getInstance().useTransaction(handle -> {
                dataMartDAO = handle.attach(DataMartDAO.class);
                dataMartDAO.insertToDimRegion(codeRegion, name, description, createdAt, expiredAt);
            });
        }


    }
}
