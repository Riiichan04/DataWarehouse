package services;

import DAO.ControlDAO;
import DAO.StagingDAO;
import DAO.WarehouseDAO;
import config.JDBIConnector;
import models.DimDate;

import java.util.List;

public class LoadDimDataWarehouseToStaging {
    private static final int PROCESS_ID = 9;
    private final ControlDAO  controlDAO;
    private final StagingDAO stagingDAO;
    private final WarehouseDAO warehouseDAO;
    public LoadDimDataWarehouseToStaging() {
        controlDAO = JDBIConnector.getControlInstance().onDemand(ControlDAO.class);
        stagingDAO = JDBIConnector.getStagingInstance().onDemand(StagingDAO.class);
        warehouseDAO = JDBIConnector.getWarehouseInstance().onDemand(WarehouseDAO.class);
    }

    public void loadDimDataWarehouseToStaging() {
        // Check tiến trình đã được hoàn thành trong ngày chưa
        if(controlDAO.checkCompletedProcess(PROCESS_ID)) return;
        // Ghi log
        int logId = controlDAO.startLoadDimProcess(PROCESS_ID);
        try {
            String companyJson = warehouseDAO.getDimCompanyJSON();
            String regionJson = warehouseDAO.getDimRegionJSON();
            String prizeJson = warehouseDAO.getDimPrizeJSON();
            stagingDAO.loadDimStaging(companyJson, regionJson, prizeJson);

            List<DimDate> dimDates = warehouseDAO.getDimDateList();
            stagingDAO.truncateTableDate();
            stagingDAO.insertDimDateBatch(dimDates);
            // Ghi log thành công
            controlDAO.recordTransformDWLog(logId, "The load of dim from data warehouse to staging was a success.", 10);
        } catch (Exception e) {
            // Ghi log thất bại
            controlDAO.recordTransformDWLog(logId, e.getMessage(), 100);
        }
    }
}
