package com.example.stock.scrapper;

import com.example.stock.model.Company;
import com.example.stock.model.Dividend;
import com.example.stock.model.ScrapedResult;
import com.example.stock.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long START_TIME = 86400;   // 60(초) * 60(분) * 24(시간)

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);    //setCompany (1)

        try {
            long now = System.currentTimeMillis() / 1000; //현재시간을 밀리세컨드로 받는 값(0.001초)이라 초단위로 바꾸기위해서 1000으로 나눈다? // 1000을 곱해야하지않나..

            //url 로 받은 값을 format 함수를 이용해 값을 얻어옴.
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();    //connection 반환 값이 Document

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0);        //table 전체

            Element tbody = tableEle.children().get(1);            //thead 는 0  tbody 는 1  tfoot 은 2

            List<Dividend> dividends = new ArrayList<>();
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);       //enum(Month) class 의 메서드
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {      //enum class 의 메서드가 false 일 경우 -1을 반환하기 때문에
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(Dividend.builder()
                        .date(LocalDateTime.of(year, month, day, 0, 0))
                        .dividend(dividend)
                        .build());

            }
            scrapResult.setDividends(dividends);     // setDividendEntities (2)


        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();  //titleEle의 text를 다 가져와서 " - " 를 기준으로 쪼갠다.
            // abc - def - xzy  split[1] 은 1번째 인덱스 "def"를 가져옴 trim()은 공백제거
            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
