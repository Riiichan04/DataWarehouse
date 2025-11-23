package services;

import DAO.ControlDAO;
import DAO.WarehouseDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.JDBIConnector;
import models.FactLottery;

import java.io.File;
import java.util.List;

public class LoadDataToDataWarehouse {
    private static final int PROCESS_LOAD_FACT_ID = 7;
    private static final int PROCESS_LOAD_DIM_ID = 8;
    private final ControlDAO controlDAO;
    private final WarehouseDAO warehouseDAO;
    private boolean checkDim = true;
    public LoadDataToDataWarehouse() {
        controlDAO = JDBIConnector.getControlInstance().onDemand(ControlDAO.class);
        warehouseDAO = JDBIConnector.getWarehouseInstance().onDemand(WarehouseDAO.class);
    }

    public void load() {
        // Kiểm tra process đã hoàn thành trong ngày chưa
        if(controlDAO.checkCompletedProcess(PROCESS_LOAD_FACT_ID)) return;
        // Kiểm tra các tiến trình bắt buộc chạy trước đã chạy thành công chưa
        if(!controlDAO.checkTransformDWDependentProcess(PROCESS_LOAD_FACT_ID)) return;

        // Load dim nếu có
        loadDimWarehouse();

        // Đã load Dim đầy đủ
        if(checkDim) {
            loadFactWarehouse();
        }
    }

    private void loadDimWarehouse() {
        final String FILE_PATH_DIM = controlDAO.getSourcePath(PROCESS_LOAD_DIM_ID);
        File dimFile = new File(FILE_PATH_DIM);
        if(dimFile.exists()) {
            if(controlDAO.checkCompletedProcess(PROCESS_LOAD_DIM_ID)) return;
            checkDim = false;
            // Ghi log
            int logId = controlDAO.startLoadProcess(PROCESS_LOAD_DIM_ID);
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(dimFile);

                // Lấy danh sách region
                JsonNode region = root.get("region");
                if (region != null && region.isArray()) {
                    warehouseDAO.loadDimRegionJson(mapper.writeValueAsString(region));
                }
                // Lấy danh sách company
                JsonNode companies = root.get("company");
                if (companies != null && companies.isArray()) {
                    warehouseDAO.loadDimCompanyJson(mapper.writeValueAsString(companies));
                }
                // Lấy danh sách prize
                JsonNode prizes = root.get("prize");
                if (prizes != null && prizes.isArray()) {
                    warehouseDAO.loadDimPrizeJson(mapper.writeValueAsString(prizes));
                }

                // Ghi log thành công
                controlDAO.recordLoadDWLog(logId, "The load of dim from file to data warehouse was a success", 10);
                checkDim = true;
            } catch (Exception e) {
                // Ghi log thất bại
                controlDAO.recordLoadDWLog(logId, e.getMessage(), 100);
            }
        }
    }

    private void loadFactWarehouse() {
        final String FILE_PATH_FACT = controlDAO.getSourcePath(PROCESS_LOAD_FACT_ID);
        File factFile = new File(FILE_PATH_FACT);
        int logId = controlDAO.startLoadProcess(PROCESS_LOAD_FACT_ID);

        ObjectMapper mapper = new ObjectMapper();
        if(factFile.exists()) {
            try {
                List<FactLottery> facts = mapper.readValue(factFile, new TypeReference<>() {});

                // Load vào data warehouse
                warehouseDAO.insertWarehouse(facts);

                // Ghi log thành công
                controlDAO.recordLoadDWLog(logId,
                        "Load Fact vào Data warehouse thành công. Số bản ghi: " + facts.size(),
                        10);
            } catch (Exception e) {
                controlDAO.recordLoadDWLog(logId,
                        "Lỗi đọc file JSON: " + e.getMessage(),
                        100);
            }
        } else {
            controlDAO.recordLoadDWLog(logId,
                    "File JSON không tồn tại: " + FILE_PATH_FACT,
                    100);
        }
    }
}
