package com.verter.scraper.model;

import com.verter.scraper.vo.Vacancy;

import java.util.List;

public interface Strategy {
    public List<Vacancy> getVacancies(String searchString);
}
