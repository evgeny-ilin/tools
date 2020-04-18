package com.verter.scraper;

import com.verter.scraper.model.HHStrategy;
import com.verter.scraper.model.Model;
import com.verter.scraper.model.Provider;
import com.verter.scraper.view.HtmlView;

/**
 * Main class
 */
public class Aggregator {
    public static void main(String[] args) {
        HtmlView view = new HtmlView();
        Model model = new Model(view, new Provider(new HHStrategy()));
        Controller controller = new Controller(model);

        view.setController(controller);
        view.userCitySelectEmulationMethod();
    }
}
