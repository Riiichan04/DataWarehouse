package services;

import enums.LogLevel;
import models.CrawlResult;
import models.DataSource;
import models.ProcessDetail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class StoreCrawlDataService {
    private static ControlService controlService = new ControlService();

    public static void storeCrawlData(List<CrawlResult> listCrawlResult) {
        try {
            String targetPath = ProcessDetail.getInstance().getTargetPath();
            File targetFile = new File(targetPath);
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                boolean mkdirs = parentDir.mkdirs();
                //Log here
            }

            String header = "date,companyName,regionName,prize8,prize7,prize6,prize5,prize4,prize3,prize2,prize1,prizeSpecial\n";
            String body = listCrawlResult.stream().map(StoreCrawlDataService::processCrawlResult).collect(Collectors.joining("\n"));

            try (FileWriter fileWriter = new FileWriter(targetFile)) {
                fileWriter.write(header);
                fileWriter.write(body);
            }
            //Log here
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.SUCCESS.getLevel(),
                    "Save data to result directory success."
            );
        } catch (Exception e) {
            //Log here
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.SUCCESS.getLevel(),
                    "Error when saving data to result directory. Error detail: " + e.getMessage()
            );
            return;
        }
    }

    private static String processCrawlResult(CrawlResult crawlResult) {
        StringBuilder result = new StringBuilder(crawlResult.getDate() + "," + crawlResult.getName() + "," + crawlResult.getRegionName() + ",");
        int size = crawlResult.getListPrize().size();
        for (int i = 0; i < size; i++) {
            //Handle case CrawlType.NORTH only have 8 prizes instead of 9
            if (i == 0 && size == 8) {
                result.append(",");
            }

            result.append(crawlResult.getListPrize().get(i));
            if (i != size - 1) result.append(",");
        }

        return result.toString();
    }
}
