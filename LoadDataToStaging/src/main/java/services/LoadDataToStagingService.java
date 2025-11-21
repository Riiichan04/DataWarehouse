package services;

import config.JDBIConnector;
import dao.StagingDAO;
import enums.LogLevel;
import models.CrawlResult;
import models.Prize;
import models.ProcessDetail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LoadDataToStagingService {
    private StagingDAO stagingDAO;
    private ControlService controlService;

    public LoadDataToStagingService() {
        this.stagingDAO = JDBIConnector.getInstance().onDemand(StagingDAO.class);
        this.controlService = new ControlService();
    }

    /**
     * Load data from inputFile to staging database
     * @param inputFile - Input csv file
     */
    public void transformAndLoadDataToStaging(File inputFile) {
        if (!inputFile.exists() || !inputFile.getName().endsWith("csv")) return;
        loadDataToStaging(transformResultToModel(inputFile));
    }

    /**
     * Convert data from csv file to CrawlResult model
     * @param inputFile - Input csv file
     * @return A list model represent data from csv file
     */
    public List<CrawlResult> transformResultToModel(File inputFile) {
        try {
            List<CrawlResult> result = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            //Skip header
            br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                //Temp, will replaced by library here
                //-1 to keep case prize 8 missing in Northen
                String[] parsedLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                String date = parsedLine[0];
                String companyName = parsedLine[1];
                String createdAt = parsedLine[2];
                List<String> prizeString = List.of(parsedLine).subList(3, parsedLine.length);
                List<Prize> listPrize = new ArrayList<>();

                for (int i = 0; i < parsedLine.length; i++) {
                    listPrize.add(new Prize("Giải " + i, prizeString.get(i)));
                }

                CrawlResult crawlResult = new CrawlResult(date, companyName, createdAt, listPrize);
                result.add(crawlResult);
            }
            br.close();
            //Log here
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.SUCCESS.getLevel(),
                    "Transform data to model success."
                    );
            return result;
        } catch (Exception e) {
            //Log here
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Error when transform data to model. Error detail: " + e.getMessage()
            );
            return null;
        }
    }

    //Load a list model into staging database
    public void loadDataToStaging(List<CrawlResult> listInput) {
        try {
            for (CrawlResult crawlResult : listInput) {
                //Only insert new data if date not exist
                if (!stagingDAO.isDateExist(crawlResult.getDate())) {
                    loadModelToStaging(crawlResult);
                }
            }

            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Load data to staging success."
            );
        }
        catch (Exception e) {
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Error when load data to staging. Error detail: " + e.getMessage()
            );
        }
    }

    //Load a model into staging database
    private int loadModelToStaging(CrawlResult input) {
        return stagingDAO.loadDataToStaging(input);
    }

}
