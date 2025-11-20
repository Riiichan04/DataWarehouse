package services;

import config.JDBIConnector;
import dao.StagingDAO;
import models.CrawlResult;
import models.Prize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LoadDataToStagingService {
    private StagingDAO stagingDAO;

    public LoadDataToStagingService() {
        this.stagingDAO = JDBIConnector.getInstance().onDemand(StagingDAO.class);
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
            return result;
        } catch (Exception e) {
            //Log here
            return null;
        }
    }

    //Load a list model into staging database
    public void loadDataToStaging(List<CrawlResult> listInput) {
        for (CrawlResult crawlResult : listInput) {
            //Only insert new data if date not exist
            if (!stagingDAO.isDateExist(crawlResult.getDate())) {
                loadModelToStaging(crawlResult);
            }
        }
    }

    //Load a model into staging database
    private int loadModelToStaging(CrawlResult input) {
        return stagingDAO.loadDataToStaging(input);
    }

}
