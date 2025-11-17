package services;

import DAO.ControlDAO;
import DAO.StagingDAO;
import config.JDBIConnector;
import models.FactLottery;
import models.LotteryStaging;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransformToDataWarehouse {
    private StagingDAO stagingDAO;
    private ControlDAO controlDAO;
    public TransformToDataWarehouse() {
        stagingDAO = JDBIConnector.getStagingInstance().onDemand(StagingDAO.class);
        controlDAO = JDBIConnector.getControlInstance().onDemand(ControlDAO.class);
    }

    private void transformFact() {
        // Check tiến trình bắt buộc đã chạy chưa
        if(!controlDAO.checkTransformDWDependentProcess()) return;

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
            }
            if (prizeSk == 0) {
                prizeSk = stagingDAO.insertPrize(stagingRecord.getPrizeName());
            }

            FactLottery fact = new FactLottery(dateSk, prizeSk, companySk, stagingRecord.getResult());
            transformedFacts.add(fact);
        }

        exportFileFactCSV(transformedFacts);
    }

    private void exportFileFactCSV(List<FactLottery> transformedFacts) {
        if (transformedFacts.isEmpty()) {
            return;
        }
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        final String FILE_PATH = controlDAO.getExportDirTransformDw() + "fact_lottery_data"+ formatter.format(currentDate) +".csv"; // load config

        // Định nghĩa tiêu đề cột (Header)
        String[] headers = {"date_sk", "prize_sk", "company_sk", "result"};

        try (FileWriter out = new FileWriter(FILE_PATH);
             CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {

            for (FactLottery fact : transformedFacts) {
                printer.printRecord(
                        fact.getDateId(),
                        fact.getPrizeId(),
                        fact.getCompanyId(),
                        fact.getResult()
                );
            }
            printer.flush();
            //ghi log
            controlDAO.recordTransformDWLog("Export Fact CSV thành công. " +
                    "Bản ghi đã được ghi vào "+FILE_PATH, 1);
        } catch (IOException e) {
            // ghi log
            controlDAO.recordTransformDWLog(e.getMessage(), 0);
        }
    }

}
