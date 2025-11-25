package services;

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
        JsonObject inputJson = jsonObject.getAsJsonObject("agg_prize_result_freq");
        int prizeId = inputJson.get("prizeId").getAsInt();
        String result = inputJson.get("result").getAsString();
        int frequency = inputJson.get("frequency").getAsInt();
        Timestamp timestamp = new Timestamp(inputJson.get("createdAt").getAsLong());

//        WarehouseConnector.getInstance().useTransaction(handle -> {
//            dataMartDAO = handle.attach(DataMartDAO.class);
            dataMartDAO.truncateAggHistoryPrizes();
            dataMartDAO.insertMostAppearReward(prizeId, result, timestamp, frequency);
//        });
    }

    private void insertResultFrequencies(JsonObject jsonObject) {
        JsonObject inputJson = jsonObject.getAsJsonObject("agg_number_week_result_freq");
        int numberOfWeek = inputJson.get("numberOfWeek").getAsInt();
        int prizeId = inputJson.get("prizeId").getAsInt();
        String result = inputJson.get("result").getAsString();
        int frequency = inputJson.get("frequency").getAsInt();
        Timestamp timestamp = new Timestamp(inputJson.get("createdAt").getAsLong());

//        WarehouseConnector.getInstance().useTransaction(handle -> {
//            dataMartDAO = handle.attach(DataMartDAO.class);
            dataMartDAO.truncateAggResultFrequencies();
            dataMartDAO.insertResultFrequencies(result, numberOfWeek, timestamp, prizeId, frequency);
//        });
    }

    private void insertPairFrequencies(JsonObject jsonObject) {
        JsonObject inputJson = jsonObject.getAsJsonObject("agg_tail_prize_freq");
        int prizeId = inputJson.get("prizeId").getAsInt();
        String result = inputJson.get("tailResult").getAsString();
        int frequency = inputJson.get("frequency").getAsInt();
        Timestamp timestamp = new Timestamp(inputJson.get("createdAt").getAsLong());

//        WarehouseConnector.getInstance().useTransaction(handle -> {
//            dataMartDAO = handle.attach(DataMartDAO.class);
            dataMartDAO.truncateAggPairFrequencies();
            dataMartDAO.insertPairFrequencies(result, prizeId, timestamp, frequency);
//        });
    }

    private void insertAggRegionResultFrequencies(JsonObject jsonObject) {
        JsonObject inputJson = jsonObject.getAsJsonObject("agg_region_result_freq");
        int regionId = inputJson.get("regionId").getAsInt();
        int prizeId = inputJson.get("prizeId").getAsInt();
        String result = inputJson.get("result").getAsString();
        int frequency = inputJson.get("frequency").getAsInt();
        Timestamp timestamp = new Timestamp(inputJson.get("createdAt").getAsLong());

//        WarehouseConnector.getInstance().useTransaction(handle -> {
//            dataMartDAO = handle.attach(DataMartDAO.class);
            dataMartDAO.truncateAggRegionResultFrequencies();
            dataMartDAO.insertRegionResultFrequencies(regionId, prizeId, result, timestamp, frequency);
//        });
    }

    public void insertRegionDim(JsonObject jsonObject) {
        JsonObject inputJson = jsonObject.getAsJsonObject("dim_region");
        String codeRegion = inputJson.get("codeRegion").getAsString();
        String name = inputJson.get("name").getAsString();
        String description = inputJson.get("description").getAsString();
        Timestamp createdAt = new Timestamp(inputJson.get("createdAt").getAsLong());
        Timestamp expiredAt = new Timestamp(inputJson.get("expiredAt").getAsLong());

//        WarehouseConnector.getInstance().useTransaction(handle -> {
//            dataMartDAO = handle.attach(DataMartDAO.class);
            dataMartDAO.truncateDimRegion();
            dataMartDAO.insertToDimRegion(codeRegion, name, description, createdAt, expiredAt);
//        });
    }
}
