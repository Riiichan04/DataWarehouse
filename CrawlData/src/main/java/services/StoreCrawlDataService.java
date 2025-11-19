package services;

import models.CrawlResult;
import models.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class StoreCrawlDataService {
    public static void storeCrawlData(DataSource dataSource, List<CrawlResult> listCrawlResult) {
        try {
            String targetPath = dataSource.getTargetPath();
            File targetFile = new File(targetPath);
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                boolean mkdirs = parentDir.mkdirs();
                //Log here
            }

            String header = "date,companyName,prize1,prize2,prize3,prize4,prize5,prize6,prize7,prize8,prizeSpecial\n";
            String body = listCrawlResult.stream().map(StoreCrawlDataService::processCrawlResult).collect(Collectors.joining("\n"));

            try (FileWriter fileWriter = new FileWriter(targetFile)) {
                fileWriter.write(header);
                fileWriter.write(body);
            }
            //Log here
        } catch (Exception e) {
            //Log here
            return;
        }
    }

    private static String processCrawlResult(CrawlResult crawlResult) {
        StringBuilder result = new StringBuilder(crawlResult.getDate() + "," + crawlResult.getName() + ",");
        int size = crawlResult.getListPrize().size();
        for (int i = 0; i < size; i++) {
            //Handle case CrawlType.NORTH only have 8 prizes instead of 9
            if (i == 7 && size == 8) {
                result.append(",");
            }

            result.append(crawlResult.getListPrize().get(i));
            if (i != size - 1) result.append(",");
        }

        return result.toString();
    }
}
