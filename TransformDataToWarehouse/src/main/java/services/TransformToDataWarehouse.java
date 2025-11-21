package services;

import DAO.ControlDAO;
import DAO.StagingDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import config.JDBIConnector;
import models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformToDataWarehouse {
    private StagingDAO stagingDAO;
    private ControlDAO controlDAO;
    public TransformToDataWarehouse() {
        stagingDAO = JDBIConnector.getStagingInstance().onDemand(StagingDAO.class);
        controlDAO = JDBIConnector.getControlInstance().onDemand(ControlDAO.class);
    }

    public void transformFact() {
        // Check tiến trình bắt buộc đã chạy chưa
        if(!controlDAO.checkTransformDWDependentProcess()) return;
        //Ghi log
        int logId = controlDAO.startTransformProcess(5);
        boolean changeDimCompany = false;
        boolean changeDimPrize = false;
        try {
            //Xử lý
            List<LotteryStaging> lotteryStagings = stagingDAO.getLotteryStaging();

            // Khởi tạo List để chứa các bản ghi Fact đã hoàn tất Transform
            List<FactLottery> transformedFacts = new ArrayList<>();

            for (LotteryStaging stagingRecord : lotteryStagings) {
                int dateSk = (stagingRecord.getDate() == null) ? -1: stagingDAO.getDateSK(stagingRecord.getDate());
                int companySk = (stagingRecord.getCompanyName() == null) ? -1: stagingDAO.getCompanySK(stagingRecord.getCompanyName());
                int prizeSk = (stagingRecord.getPrizeName() == null) ? -1: stagingDAO.getPrizeSK(stagingRecord.getPrizeName());

                if (companySk == 0) {
                    companySk = stagingDAO.insertCompany(stagingRecord.getCompanyName());
                    changeDimCompany = true;

                }
                if (prizeSk == 0) {
                    prizeSk = stagingDAO.insertPrize(stagingRecord.getPrizeName());
                    changeDimPrize = true;
                }

                FactLottery fact = new FactLottery(dateSk, prizeSk, companySk, stagingRecord.getResult());
                transformedFacts.add(fact);
            }

            exportFileFactJSON(transformedFacts, logId);
            if (changeDimCompany || changeDimPrize) {
                exportFileDimJSON(changeDimCompany, changeDimPrize);
            }
        } catch (Exception e) {
            controlDAO.recordTransformDWLog(logId, e.getMessage(), 100);
        }

    }

    private void exportFileFactJSON(List<FactLottery> transformedFacts, int logId) {
        if (transformedFacts.isEmpty()) {
            return;
        }

        final String FILE_PATH = controlDAO.getExportDirTransformDw(5); // đường dẫn file JSON

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Ghi danh sách ra file JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), transformedFacts);

            // Ghi log thành công
            controlDAO.recordTransformDWLog(logId,
                    "Export Fact JSON thành công. Bản ghi đã được ghi vào " + FILE_PATH,
                    10);

        } catch (IOException e) {
            // Ghi log lỗi
            controlDAO.recordTransformDWLog(logId, e.getMessage(), 100);
        }
    }

    private void exportFileDimJSON(boolean changeDimCompany, boolean changeDimPrize) {
        int logId = controlDAO.startTransformProcess(6);

        final String FILE_PATH = controlDAO.getExportDirTransformDw(6); // đường dẫn file JSON

        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> dimJson = new HashMap<>();
            if(changeDimCompany) {
                List<DimCompany> dimCompanies = stagingDAO.getDimCompany();
                dimJson.put("company", dimCompanies);
                List<DimRegion> dimRegion = stagingDAO.getDimRegion();
                dimJson.put("region", dimRegion);
            }
            if(changeDimPrize) {
                List<DimPrize> dimPrizes = stagingDAO.getDimPrize();
                dimJson.put("prize", dimPrizes);
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), dimJson);

            // Ghi log thành công
            controlDAO.recordTransformDWLog(logId,
                    "Export Dim JSON thành công. Bản ghi đã được ghi vào " + FILE_PATH,
                    10);

        } catch (IOException e) {
            // Ghi log lỗi
            controlDAO.recordTransformDWLog(logId, e.getMessage(), 100);
        }
    }

}
