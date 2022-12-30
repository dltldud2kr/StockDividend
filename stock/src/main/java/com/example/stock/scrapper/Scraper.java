package com.example.stock.scrapper;

import com.example.stock.model.Company;
import com.example.stock.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
