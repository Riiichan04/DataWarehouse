package services;

import models.CrawlResult;
import models.DataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CrawlDataService {
    public static CrawlResult crawl(DataSource dataSource, int offset) {
        String url = dataSource.getUrl();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36\"")
                    .timeout(600000).get();   //Timeout 10 minutes
            return crawlNorthByOffset(doc, offset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static CrawlResult crawlNorthByOffset(Document doc, int offset) {
        // JS: date.getTime() - dateOffset * (...)
        LocalDate today = LocalDate.now();
        LocalDate resultDate = today.minusDays(offset);
        String dateString = resultDate.toString();

        // JS: document.querySelectorAll(".table-result")[dateOffset]
        Elements tables = doc.select(".table-result");

        //If no data to crawl
        if (offset >= tables.size()) {
            return null;
        }

        Element table = tables.get(offset);
        // JS: table.querySelectorAll("tr td").forEach(...)
        Elements tds = table.select("tr td");
        List<String> listResult = new ArrayList<>();

        for (int i = 0; i < tds.size(); i++) {
            // JS: i % 2 == 0 && i > 0
            if (i % 2 == 0 && i > 0) {
                // JS: e.innerText -> Java: element.text()
                String text = tds.get(i).text();
                listResult.add(text);
            }
        }

        //Temp result
        return new CrawlResult("Xổ số miền Bắc", dateString, listResult);
    }
}
