import config.LoadConfigOperator;
import models.CrawlResult;
import models.DataSource;
import services.CrawlDataService;
import services.StoreCrawlDataService;

import java.util.List;

public class CrawlDataOperator {
    public static void main(String[] args) {
        //Will be changed later
        int offset = 0;
        if (args.length == 0) {
            System.out.println("First parameter is required between: \"NORTH\", \"MIDDLE\", \"SOUTH\" ");
            return;
        }
        String type = args[0];
        try {
            String argsOffset = args[1];
            offset = Integer.parseInt(argsOffset);
        } catch (Exception ignored) {
        }

        LoadConfigOperator configLoader = new LoadConfigOperator();
        configLoader.loadConfig(type, offset);
        DataSource dataSource = configLoader.getDataSource();

        List<CrawlResult> listResult = CrawlDataService.crawl(dataSource, offset);
        StoreCrawlDataService.storeCrawlData(listResult);
    }
}
