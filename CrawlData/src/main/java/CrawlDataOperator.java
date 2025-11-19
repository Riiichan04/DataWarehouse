import config.LoadConfigOperator;
import models.CrawlResult;
import models.DataSource;
import services.CrawlDataService;

import java.util.List;

public class CrawlDataOperator {
    public static void main(String[] args) {
        int offset = 0;
        DataSource dataSource = new LoadConfigOperator().loadConfig();
        List<CrawlResult> listSource = CrawlDataService.crawl(dataSource, offset);
    }
}
