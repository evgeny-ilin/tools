package com.verter.scraper.model;

import com.verter.scraper.vo.Vacancy;

import java.util.Collections;
import java.util.List;

public class Provider {
    private Strategy strategy;

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Provider(Strategy strategy) {
        this.strategy = strategy;
    }

    public List<Vacancy> getJavaVacancies(String searchString) {
        if(searchString == null) return Collections.emptyList();
        return strategy.getVacancies(searchString);
    }
}
