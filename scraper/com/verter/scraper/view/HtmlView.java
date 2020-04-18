package com.verter.scraper.view;

import com.verter.scraper.Controller;
import com.verter.scraper.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HtmlView implements View {
    private Controller controller;
    private final String filePath = "./" + this.getClass().getPackage().getName().replace('.', '/') + "/vacancies.html";

    protected Document getDocument() throws IOException {
        //Возвращаем файл с вакансиями
        return Jsoup.parse(new File(filePath),"UTF-8");
    }
    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Самара");
    }

    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            updateFile(getUpdatedFileContent(vacancies));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) {
        Document document = null;

        try {
            document = getDocument();
        } catch (IOException e) {
            e.printStackTrace();
            return "Some exception occurred";
        }

        //Выдергиваем оттуда шаблон
        Element eTemplateSrc = document.getElementsByClass("template").first();
        //Сохраняем шаблон в отдельной переменной, так как будем его менять, чтобы не затереть изменения в файле
        Element template = eTemplateSrc.clone();
        template.removeClass("template");
        template.removeAttr("style");

        //Удаляем все вакансии
        document.select("tr[class=vacancy]").not("tr[class=vacancy template]").remove();

        //Создаем элемент по шаблону
        Element element = template.clone();

        for (Vacancy vacancy:vacancies) {
            element.getElementsByClass("city").first().text(vacancy.getCity());
            element.getElementsByClass("companyName").first().text(vacancy.getCompanyName());
            element.getElementsByClass("salary").first().text(vacancy.getSalary());

            Element link = element.getElementsByTag("a").first();
            link.text(vacancy.getTitle());
            link.attr("href", vacancy.getUrl());

            eTemplateSrc.before(element.outerHtml());
        }

        return document.html();
    }

    private void updateFile(String lines) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(lines);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
