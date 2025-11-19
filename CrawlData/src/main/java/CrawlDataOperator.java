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
        String argsOffset = args[0];
        try {
            offset = Integer.parseInt(argsOffset);
        } catch (NumberFormatException ignored) {
        }
        
        DataSource dataSource = new LoadConfigOperator().loadConfig();
        List<CrawlResult> listResult = CrawlDataService.crawl(dataSource, offset);
        StoreCrawlDataService.storeCrawlData(dataSource, listResult);
    }
}
