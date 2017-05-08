package de.tu_dortmund.pgdfs.blscrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Domi on 08.05.2017.
 */
public class BLSCrawler {
    public static final String REQUEST_URL = "http://www.ernaehrung.de/lebensmittel/suche/";

    private static final Logger LOGGER = LoggerFactory.getLogger(BLSCrawler.class);

    public static void main(String[] args) throws IOException {
        getFoodURLs();
    }

    public static Collection<URL> getFoodURLs() throws IOException {
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
        LOGGER.debug("Fetched " + foodList.size() + " food URLs.");
        return foodList;
    }
}
