package services;

import config.JDBIConnector;
import dao.StagingDAO;
import models.CrawlResult;

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

    public List<CrawlResult> transformResultToModel(String inputPath) {
        try {
            List<CrawlResult> result = new ArrayList<>();
            File inputFile = new File(inputPath);
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            //Skip header
            br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                //Temp, will replaced by library here
                String[] parsedLine = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                CrawlResult crawlResult = new CrawlResult(parsedLine);
                result.add(crawlResult);
            }
            br.close();
            return result;
        }
        catch (Exception e) {
            //Log here
            return null;
        }
    }

    public int loadModelToStaging(CrawlResult input) {
        return stagingDAO.loadDataToStaging(input);
    }

}
