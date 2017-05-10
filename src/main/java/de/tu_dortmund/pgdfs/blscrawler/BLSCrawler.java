package de.tu_dortmund.pgdfs.blscrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public class BLSCrawler {
    public static final String REQUEST_URL = "http://www.ernaehrung.de/lebensmittel/suche/";

    private static final Logger LOGGER = LoggerFactory.getLogger(BLSCrawler.class);

    public static void main(String[] args) throws IOException {
        getFoodURLs();
    }

    public static List<URL> getFoodURLs() throws IOException {
        Document resultsDoc = Jsoup.connect(REQUEST_URL)
                .data("nameInput", "")
                .data("origin", "bls")
                .data("language", "de")
                .maxBodySize(0)
                .post();
        Iterator<Element> it = resultsDoc.getElementById("wrapper").getElementsByClass("list-group-item").iterator();
        List<URL> foodList = new LinkedList<URL>();
        while (it.hasNext()) {
            foodList.add(new URL(it.next().attr("href")));
        }
        LOGGER.info("Fetched " + foodList.size() + " food URLs.");
        return foodList;
    }

    public static String crawlFoodURLs(List<URL> foodURLs) {
        LOGGER.info("Start crawling food URLs");
        for (URL foodURL : foodURLs) {
            LOGGER.debug("Crawling food URL " + (foodURLs.indexOf(foodURL)+1) + " of " + foodURLs.size());
            crawlFoodURL(foodURL); //TODO append to return value
        }
        LOGGER.info("Finished crawling food URLs");
        return "";
    }

    public static String crawlFoodURL(URL foodURL) {
        return "";
    }
}
