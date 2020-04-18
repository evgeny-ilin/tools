package com.verter.scraper.model;

import com.verter.scraper.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private String getUrlFormat = String.format(URL_FORMAT,"Самара",3);

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        List<Vacancy> vacancies = new ArrayList<>();
        Document html = null;

        for (int pageNumber = 0; ;pageNumber++) {
            try {
                html = getDocument(searchString, pageNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = html.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

            if (elements.size() == 0) break;

            for (Element element : elements) {
                Vacancy vacancy = new Vacancy();
                vacancy.setTitle(element.getElementsByAttributeValueContaining("data-qa", "vacancy-serp__vacancy-title").text().trim());
                vacancy.setCity(element.getElementsByAttributeValueContaining("data-qa", "vacancy-serp__vacancy-address").text().trim());
                vacancy.setCompanyName(element.getElementsByAttributeValueContaining("data-qa", "vacancy-serp__vacancy-employer").text().trim());
                vacancy.setUrl(element.getElementsByAttributeValueContaining("data-qa", "vacancy-serp__vacancy-title").attr("href").trim());
                vacancy.setSalary(element.getElementsByAttributeValueContaining("data-qa", "vacancy-serp__vacancy-compensation").text().trim());
                vacancy.setSiteName(URL_FORMAT);

                vacancies.add(vacancy);
            }
        }

        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {

        return Jsoup.connect(String.format(URL_FORMAT,searchString,page))
                .get();
    }
}
