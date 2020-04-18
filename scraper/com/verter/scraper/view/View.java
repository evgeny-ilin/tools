package com.verter.scraper.view;

import com.verter.scraper.Controller;
import com.verter.scraper.vo.Vacancy;

import java.util.List;

public interface View {
    void update(List<Vacancy> vacancies);
    void setController(Controller controller);
}
