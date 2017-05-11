package de.tu_dortmund.pgdfs.blscrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public class BLSCrawler {
    private static final String REQUEST_URL = "http://www.ernaehrung.de/lebensmittel/suche/";
    private static final Logger LOGGER = LoggerFactory.getLogger(BLSCrawler.class);
    private static final HashMap<String, BLSNutrient> WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP = new HashMap();

    static {
        //Hauptnährstoffe
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Broteinheiten", BLSNutrient.BROTEINHEITEN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kilokalorien", BLSNutrient.ENERGIE_KILOKALORIEN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kilojoule", BLSNutrient.ENERGIE_KILOJOULE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eiweiß", BLSNutrient.EIWEISS_PROTEIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fett", BLSNutrient.FETT);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kohlenhydrate", BLSNutrient.KOHLENHYDRATE_RESORBIERBAR);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Alkohol", BLSNutrient.ALKOHOL_ETHANOL);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Wasser", BLSNutrient.WASSER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe gesamt", BLSNutrient.BALLASTSTOFFE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cholesterin", BLSNutrient.CHOLESTERIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mineralstoffe", BLSNutrient.MINERALSTOFFE_ROHASCHE);

        //Vitamine
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin A Retinol", BLSNutrient.VITAMIN_A_RETINOL);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin D", BLSNutrient.VITAMIN_D_CALCIFEROLE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin E Aktiv.", BLSNutrient.VITAMIN_E_ALPHA_TOCOPHEROLAEQUIVALENT); //TODO auf Webseite immer gleich VITAMIN_E_ALPHA_TOCOPHEROL?
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Folsäure", BLSNutrient.VITAMIN_B9_GESAMTE_FOLSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B1", BLSNutrient.VITAMIN_B1_THIAMIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B2", BLSNutrient.VITAMIN_B2_RIBOFLAVIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B6", BLSNutrient.VITAMIN_B6_PYRIDOXIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin C", BLSNutrient.VITAMIN_C_ASCORBINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("a-Tocopherol", BLSNutrient.VITAMIN_E_ALPHA_TOCOPHEROL);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin K", BLSNutrient.VITAMIN_K_PHYLLOCHINON);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Nicotinamid", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Pantothensäure", BLSNutrient.VITAMIN_B5_PANTOTHENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Biotin", BLSNutrient.VITAMIN_B7_BIOTIN_VITAMIN_H);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B12", BLSNutrient.VITAMIN_B12_COBALAMIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Retinoläquivalent", BLSNutrient.VITAMIN_A_RETINOLAEQUIVALENT);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("ß-Carotin", BLSNutrient.VITAMIN_A_BETA_CAROTIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Niacinäquivalent", BLSNutrient.VITAMIN_B3_NIACINAEQUIVALENT);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("freies Folsäureäquivalent", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("freie Folsäure", );

        //Mineralstoffe und Spurenelemente
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Natrium", BLSNutrient.NATRIUM);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kalium", BLSNutrient.KALIUM);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Magnesium", BLSNutrient.MAGNESIUM);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Calcium", BLSNutrient.CALCIUM);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eisen", BLSNutrient.EISEN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Phosphor", BLSNutrient.PHOSPHOR);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kupfer", BLSNutrient.KUPFER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Zink", BLSNutrient.ZINK);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Chlorid", BLSNutrient.CHLORID);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fluorid", BLSNutrient.FLUORID);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Jodid", BLSNutrient.IODID);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Selen", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mangan", BLSNutrient.MANGAN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Schwefel", BLSNutrient.SCHWEFEL);

        //Aminosäuren
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arginin", BLSNutrient.ARGININ);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cystin", BLSNutrient.CYSTEIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Histidin", BLSNutrient.HISTIDIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Isoleucin", BLSNutrient.ISOLEUCIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Leucin", BLSNutrient.LEUCIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lysin", BLSNutrient.LYSIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Methionin", BLSNutrient.METHIONIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Phenylalanin", BLSNutrient.PHENYLALANIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Threonin", BLSNutrient.THREONIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Tryptophan", BLSNutrient.TRYPTOPHAN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Tyrosin", BLSNutrient.TYROSIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Valin", BLSNutrient.VALIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Alanin", BLSNutrient.ALANIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Asparaginsäure", BLSNutrient.ASPARAGINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glutaminsäure", BLSNutrient.GLUTAMINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycin", BLSNutrient.GLYCIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Prolin", BLSNutrient.PROLIN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Serin", BLSNutrient.SERIN);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. essent. Aminosäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("essent. Aminosäuren", BLSNutrient.ESSENTIELLE_AMINOSAEUREN);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. n. essent. Aminosäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("n. essent. Aminosäuren", BLSNutrient.NICHTESSENTIELLE_AMINOSAEUREN);

        //Fettsäuren
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ges. Fettsäuren", BLSNutrient.GESAETTIGTE_FETTSAEUREN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("mehrf. unges. Fettsäuren", BLSNutrient.MEHRFACH_UNGESAETTIGTE_FETTSAEUREN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("einfach unges. Fettsäuren", BLSNutrient.EINFACH_UNGESAETTIGTE_FETTSAEUREN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Buttersäure", BLSNutrient.BUTANSAEURE_BUTTERSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Capronsäure", BLSNutrient.HEXANSAEURE_CAPRONSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Caprylsäure", BLSNutrient.OCTANSAEURE_CAPRYLSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Caprinsäure", BLSNutrient.DECANSAEURE_CAPRINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Laurinsäure", BLSNutrient.DODECANSAEURE_LAURINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Myristinsäure", BLSNutrient.TETRADECANSAEURE_MYRISTINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C15:O Fettsäure", BLSNutrient.PENTADECANSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Palmitinsäure", BLSNutrient.HEXADECANSAEURE_PALMITINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Margarinsäure", BLSNutrient.HEPTADECANSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stearinsäure", BLSNutrient.OCTADECANSAEURE_STEARINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arachinsäure", BLSNutrient.EICOSANSAEURE_ARACHINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Behensäure", BLSNutrient.DOCOSANSAEURE_BEHENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lignocerinsäure", BLSNutrient.TETRACOSANSAEURE_LIGNOCERINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Palmitoleinsäure", BLSNutrient.HEXADECENSAEURE_PALMITOLEINSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ölsäure", BLSNutrient.OCTADECENSAEURE_OELSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosensäure", BLSNutrient.EICOSENSAEURE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:1 Fettsäure", ); //TODO ?
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C14:1 Fettsäure", ); //TODO BLSNutrient.TETRADECENSAEURE?
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C24:1 Fettsäure", ); //TODO ?
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolsäure", BLSNutrient.OCTADECADIENSAEURE_LINOLSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolensäure", BLSNutrient.OCTADECATRIENSAEURE_LINOLENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arachidonsäure", BLSNutrient.EICOSATETRAENSAEURE_ARACHIDONSAEURE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C18:4 Fettsäure", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C20:5 N-3 Fettsäure", BLSNutrient.EICOSAPENTAENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:5 N-3 Fettsäure", BLSNutrient.DOCOSAPENTAENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:6 N-3 Fettsäure", BLSNutrient.DOCOSAHEXAENSAEURE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C16:2 Fettsäure", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. gesättigte Fettsäuren", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. einfach unges. Fettsäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Nonadecatriensäure", BLSNutrient.NONADECATRIENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosadiensäure", BLSNutrient.EICOSADIENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosatriensäure", BLSNutrient.EICOSATRIENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosadiensäure", BLSNutrient.DOCOSADIENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosatriensäure", BLSNutrient.DOCOSATRIENSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosatetraensäure", BLSNutrient.DOCOSATETRAENSAEURE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. mehrfach unges. Fettsäuren", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. kurzkettige Fettsäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("kurzkettige Fettsäuren", BLSNutrient.KURZKETTIGE_FETTSAEUREN);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. mittelkettige Fettsäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("mittelkettige Fettsäuren", BLSNutrient.MITTELKETTIGE_FETTSAEUREN);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. langkettige Fettsäuren", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("langkettige Fettsäuren", BLSNutrient.LANGKETTIGE_FETTSAEUREN);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycerin + Lipoide", BLSNutrient.GLYCERIN_UND_LIPOIDE);

        //Spezielle Kohlenhydrate
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Sorbit", BLSNutrient.SORBIT);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glucose", BLSNutrient.GLUCOSE_TRAUBENZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fructose", BLSNutrient.FRUCTOSE_FRUCHTZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Saccharose", BLSNutrient.SACCHAROSE_RUEBENZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lactose", BLSNutrient.LACTOSE_MILCHZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stärke", BLSNutrient.STAERKE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Gesamtzucker", BLSNutrient.ZUCKER_GESAMT);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Maltose", BLSNutrient.MALTOSE_MALZZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Galactose", BLSNutrient.GALACTOSE_SCHLEIMZUCKER);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycogen", BLSNutrient.GLYKOGEN_TIERISCHE_STAERKE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Pentosan", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Hexosan", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cellulose", BLSNutrient.CELLULOSE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Polyuronsäure", BLSNutrient.POLY_URONSAEURE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mannit", BLSNutrient.MANNIT);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Xylit", BLSNutrient.XYLIT);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Zuckeralkohole", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Summe Zuckeralkohole", BLSNutrient.SUMME_ZUCKERALKOHOLE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Monosaccharide", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Monosaccharide", BLSNutrient.MONOSACCHARIDE_1_M);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Disaccharide", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Disaccharide", BLSNutrient.DISACCHARIDE_2_M);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Oligosaccharide resorb.", BLSNutrient.OLIGOSACCHARIDE_RESORBIERBAR_3_9_M);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Oligosaccharide n. resorb.", BLSNutrient.OLIGOSACCHARIDE_NICHT_RESORBIERBAR);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Polysaccharide", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Polysaccharide", BLSNutrient.POLYSACCHARIDE_MEHR_ALS_9_M);

        //Sonstiges
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe wasserl.", BLSNutrient.WASSERLOESLICHE_BALLASTSTOFFE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe w.unlösl.", BLSNutrient.WASSERUNLOESLICHE_BALLASTSTOFFE);
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lignin", BLSNutrient.LIGNIN);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Purinbasen-Stickstoff", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kochsalz", BLSNutrient.GESAMT_KOCHSALZ);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Küchenabfälle", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Eiweißstoffe", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("tierisches Eiweiß", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("pflanzliches Eiweiß", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Harnsäure", BLSNutrient.HARNSAEURE);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. organischen Säuren", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mol-Diff. Kationen-Anionen		mä", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stickstoffaktor", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fettsäurenanteil", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mineralstoffanteil", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("P/S Verhältnis", BLSNutrient.P_S_VERHAELTNIS);
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Biolog. Wertigkeit", );
//        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("fruktosefreie Broteinheiten", );
        WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("durchschn. Verzehr", BLSNutrient.MITTLERE_PORTIONSGROESSE);

        //Allergene und Zusatzstoffe
        //WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fisch und Fischerzeugnisse", );
    }

    public static void main(String[] args) throws IOException {
        List<URL> foodURLs = getFoodURLs();
        crawlFoodURLs(foodURLs);
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

    public static String crawlFoodURLs(List<URL> foodURLs) throws IOException {
        LOGGER.info("Start crawling food URLs");
        for (URL foodURL : foodURLs) {
            LOGGER.debug("Crawling food URL " + (foodURLs.indexOf(foodURL)+1) + " of " + foodURLs.size());
            crawlFoodURL(foodURL); //TODO append to return value
        }
        LOGGER.info("Finished crawling food URLs");
        return "";
    }

    public static String crawlFoodURL(URL foodURL) throws IOException {
        EnumMap<BLSNutrient, String> food = new EnumMap(BLSNutrient.class);
        Document resultsDoc = Jsoup.connect(foodURL.toString())
                .get();
        List<Elements> tableRows = resultsDoc.getElementById("wrapper").getElementsByClass("table table-condensed table-striped table-bordered").stream().map(e -> e.getElementsByTag("tr")).collect(Collectors.toList());
        for(Elements elements : tableRows) {
            Iterator<Element> it = elements.iterator();
            while(it.hasNext()) {
                Element next = it.next();
                Elements tableData = next.getElementsByTag("td");
                if(!tableData.isEmpty()) {
                    if (!WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.containsKey(tableData.get(0).text())) {
                        //LOGGER.warn("Unmapped nutrient \"" + tableData.get(0).text() + "\" " + tableData.get(1).text() + " " + (tableData.size()>=3 ? tableData.get(2).text(): "")); //TODO
                    } else {
                        //System.out.println(tableData.get(0).text() + ", " + tableData.get(1).text() + " " + tableData.get(2).text());
                        food.put(WEBSITE_LONG_NAME_TO_BLS_NUTRIENT_MAP.get(tableData.get(0).text()), tableData.get(1).text()); //TODO string to double and unit conversion
                    }
                }
            }
        }
        return "";
    }
}
