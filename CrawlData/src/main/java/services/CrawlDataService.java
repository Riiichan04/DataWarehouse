package services;

import enums.CrawlType;
import enums.LogLevel;
import models.CrawlResult;
import models.DataSource;
import models.ProcessDetail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrawlDataService {
    static ControlService controlService = new ControlService();

    public static List<CrawlResult> crawl(DataSource dataSource, int offset) {
        //Always >= 0
        offset = Math.abs(offset);
        CrawlType crawlType = dataSource.getType();

        String url = dataSource.getUrl();
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36\"")
                    .timeout(600000).get();   //Timeout 10 minutes
            if (crawlType == CrawlType.NORTH) {
                controlService.addNewLog(
                        ProcessDetail.getInstance().getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.SUCCESS.getLevel(),
                        "Crawl north data success"
                );
                return List.of(Objects.requireNonNull(crawlNorthByOffset(doc, offset)));
            }
            else {
                controlService.addNewLog(
                        ProcessDetail.getInstance().getProcessId(),
                        new Timestamp(System.currentTimeMillis()),
                        new Timestamp(System.currentTimeMillis()),
                        LogLevel.SUCCESS.getLevel(),
                        "Crawl " + (crawlType == CrawlType.MIDDLE ? "middle" : "southern") + " data success"
                );
                return crawlSouthOrMiddleByOffset(doc, offset, crawlType);
            }
        } catch (IOException e) {
            controlService.addNewLog(
                    ProcessDetail.getInstance().getProcessId(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    LogLevel.ERROR.getLevel(),
                    "Error when crawl data. Error detail: " + e.getMessage()
            );
            return null;
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

        for (int i = tds.size() - 1; i >= 0; i--) {
            // JS: i % 2 == 0 && i > 0
            if (i % 2 == 0 && i > 0) {
                // JS: e.innerText -> Java: element.text()
                String text = tds.get(i).text();
                listResult.add(text);
            }
        }

        //Temp result
        return new CrawlResult("Xổ số miền Bắc", dateString, "Miền Bắc", listResult);
    }

    private static List<CrawlResult> crawlSouthOrMiddleByOffset(Document doc, int offset, CrawlType type) {
        // JS: date.getTime() - dateOffset * (...)
        LocalDate today = LocalDate.now();
        LocalDate resultDate = today.minusDays(offset);
        String dateString = resultDate.toString();

        // JS: document.querySelectorAll(".table-result")[dateOffset]
        Elements tables = doc.select(".table-result");

        // If no data to crawl
        if (offset >= tables.size()) {
            return new ArrayList<>();
        }

        Element table = tables.get(offset);

        // JS: table.querySelectorAll("thead th h3>*").forEach(e => listHeader.push(e.title))
        List<String> listHeader = new ArrayList<>();
        Elements headerElements = table.select("thead th h3 > *");
        for (Element e : headerElements) {
            listHeader.add(e.attr("title"));
        }

        // JS: const listEle = table.querySelectorAll("tr td")
        Elements listEle = table.select("tr td");

        List<List<String>> listResultTemp = new ArrayList<>();
        // JS: flag < 3 (Assuming number of columns matches number of headers)
        int numberOfProvinces = listHeader.size();

        for (int flag = 0; flag < numberOfProvinces; flag++) {
            List<String> temp = new ArrayList<>();
            // JS: for (let i = 0; i < 9; i++)
            for (int i = 0; i < 9; i++) {
                // JS: temp.push(listEle[3*i+flag].innerText) -> Logic: index = numProvinces * i + flag
                int index = numberOfProvinces * i + flag;
                if (index < listEle.size()) {
                    temp.add(listEle.get(index).text());
                }
            }
            // JS: listResult.push(temp)
            listResultTemp.add(temp);
        }

        // JS: for (let i = 0; i < listHeader.length; i++) { finalResult.push(...) }
        List<CrawlResult> finalResult = new ArrayList<>();
        for (int i = 0; i < listHeader.size(); i++) {
            finalResult.add(new CrawlResult(
                    listHeader.get(i),   // name
                    dateString,          // date
                    (type == CrawlType.MIDDLE ? "Miền Trung" : "Miền Nam"),
                    listResultTemp.get(i) // listPrize
            ));
        }

        return finalResult;
    }
}
