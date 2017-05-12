package de.tu_dortmund.pgdfs.blscrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public class BLSCrawler {
    private static final String REQUEST_URL = "http://www.ernaehrung.de/lebensmittel/suche/";
    private static final Logger LOGGER = LoggerFactory.getLogger(BLSCrawler.class);
    private static final HashMap<String, BLSNutrient> WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP = new HashMap<>();
    private static final Set<String> WEBSITE_IGNORED_NUTRIENT_LONG_NAMES = new HashSet<>();

    static {
        //Hauptnährstoffe
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Broteinheiten", BLSNutrient.BROTEINHEITEN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kilokalorien", BLSNutrient.ENERGIE_KILOKALORIEN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kilojoule", BLSNutrient.ENERGIE_KILOJOULE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eiweiß", BLSNutrient.EIWEISS_PROTEIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fett", BLSNutrient.FETT);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kohlenhydrate", BLSNutrient.KOHLENHYDRATE_RESORBIERBAR);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Alkohol", BLSNutrient.ALKOHOL_ETHANOL);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Wasser", BLSNutrient.WASSER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe gesamt", BLSNutrient.BALLASTSTOFFE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cholesterin", BLSNutrient.CHOLESTERIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mineralstoffe", BLSNutrient.MINERALSTOFFE_ROHASCHE);

        //Vitamine
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin A Retinol", BLSNutrient.VITAMIN_A_RETINOL);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin D", BLSNutrient.VITAMIN_D_CALCIFEROLE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin E Aktiv.", BLSNutrient.VITAMIN_E_ALPHA_TOCOPHEROLAEQUIVALENT); //TODO auf Webseite immer gleich VITAMIN_E_ALPHA_TOCOPHEROL?
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Folsäure", BLSNutrient.VITAMIN_B9_GESAMTE_FOLSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B1", BLSNutrient.VITAMIN_B1_THIAMIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B2", BLSNutrient.VITAMIN_B2_RIBOFLAVIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B6", BLSNutrient.VITAMIN_B6_PYRIDOXIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin C", BLSNutrient.VITAMIN_C_ASCORBINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("a-Tocopherol", BLSNutrient.VITAMIN_E_ALPHA_TOCOPHEROL);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin K", BLSNutrient.VITAMIN_K_PHYLLOCHINON);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Nicotinamid", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Pantothensäure", BLSNutrient.VITAMIN_B5_PANTOTHENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Biotin", BLSNutrient.VITAMIN_B7_BIOTIN_VITAMIN_H);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Vitamin B12", BLSNutrient.VITAMIN_B12_COBALAMIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Retinoläquivalent", BLSNutrient.VITAMIN_A_RETINOLAEQUIVALENT);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("ß-Carotin", BLSNutrient.VITAMIN_A_BETA_CAROTIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Niacinäquivalent", BLSNutrient.VITAMIN_B3_NIACINAEQUIVALENT);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("freies Folsäureäquivalent", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("freie Folsäure", );

        //Mineralstoffe und Spurenelemente
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Natrium", BLSNutrient.NATRIUM);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kalium", BLSNutrient.KALIUM);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Magnesium", BLSNutrient.MAGNESIUM);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Calcium", BLSNutrient.CALCIUM);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eisen", BLSNutrient.EISEN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Phosphor", BLSNutrient.PHOSPHOR);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kupfer", BLSNutrient.KUPFER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Zink", BLSNutrient.ZINK);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Chlorid", BLSNutrient.CHLORID);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fluorid", BLSNutrient.FLUORID);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Jodid", BLSNutrient.IODID);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Selen", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mangan", BLSNutrient.MANGAN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Schwefel", BLSNutrient.SCHWEFEL);

        //Aminosäuren
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arginin", BLSNutrient.ARGININ);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cystin", BLSNutrient.CYSTEIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Histidin", BLSNutrient.HISTIDIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Isoleucin", BLSNutrient.ISOLEUCIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Leucin", BLSNutrient.LEUCIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lysin", BLSNutrient.LYSIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Methionin", BLSNutrient.METHIONIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Phenylalanin", BLSNutrient.PHENYLALANIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Threonin", BLSNutrient.THREONIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Tryptophan", BLSNutrient.TRYPTOPHAN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Tyrosin", BLSNutrient.TYROSIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Valin", BLSNutrient.VALIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Alanin", BLSNutrient.ALANIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Asparaginsäure", BLSNutrient.ASPARAGINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glutaminsäure", BLSNutrient.GLUTAMINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycin", BLSNutrient.GLYCIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Prolin", BLSNutrient.PROLIN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Serin", BLSNutrient.SERIN);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. essent. Aminosäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("essent. Aminosäuren", BLSNutrient.ESSENTIELLE_AMINOSAEUREN);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. n. essent. Aminosäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("n. essent. Aminosäuren", BLSNutrient.NICHTESSENTIELLE_AMINOSAEUREN);

        //Fettsäuren
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ges. Fettsäuren", BLSNutrient.GESAETTIGTE_FETTSAEUREN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("mehrf. unges. Fettsäuren", BLSNutrient.MEHRFACH_UNGESAETTIGTE_FETTSAEUREN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("einfach unges. Fettsäuren", BLSNutrient.EINFACH_UNGESAETTIGTE_FETTSAEUREN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Buttersäure", BLSNutrient.BUTANSAEURE_BUTTERSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Capronsäure", BLSNutrient.HEXANSAEURE_CAPRONSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Caprylsäure", BLSNutrient.OCTANSAEURE_CAPRYLSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Caprinsäure", BLSNutrient.DECANSAEURE_CAPRINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Laurinsäure", BLSNutrient.DODECANSAEURE_LAURINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Myristinsäure", BLSNutrient.TETRADECANSAEURE_MYRISTINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C15:O Fettsäure", BLSNutrient.PENTADECANSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Palmitinsäure", BLSNutrient.HEXADECANSAEURE_PALMITINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Margarinsäure", BLSNutrient.HEPTADECANSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stearinsäure", BLSNutrient.OCTADECANSAEURE_STEARINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arachinsäure", BLSNutrient.EICOSANSAEURE_ARACHINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Behensäure", BLSNutrient.DOCOSANSAEURE_BEHENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lignocerinsäure", BLSNutrient.TETRACOSANSAEURE_LIGNOCERINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Palmitoleinsäure", BLSNutrient.HEXADECENSAEURE_PALMITOLEINSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ölsäure", BLSNutrient.OCTADECENSAEURE_OELSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosensäure", BLSNutrient.EICOSENSAEURE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:1 Fettsäure", ); //TODO ?
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C14:1 Fettsäure", ); //TODO BLSNutrient.TETRADECENSAEURE?
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C24:1 Fettsäure", ); //TODO ?
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolsäure", BLSNutrient.OCTADECADIENSAEURE_LINOLSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolensäure", BLSNutrient.OCTADECATRIENSAEURE_LINOLENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arachidonsäure", BLSNutrient.EICOSATETRAENSAEURE_ARACHIDONSAEURE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C18:4 Fettsäure", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C20:5 N-3 Fettsäure", BLSNutrient.EICOSAPENTAENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:5 N-3 Fettsäure", BLSNutrient.DOCOSAPENTAENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:6 N-3 Fettsäure", BLSNutrient.DOCOSAHEXAENSAEURE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C16:2 Fettsäure", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. gesättigte Fettsäuren", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. einfach unges. Fettsäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Nonadecatriensäure", BLSNutrient.NONADECATRIENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosadiensäure", BLSNutrient.EICOSADIENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Eicosatriensäure", BLSNutrient.EICOSATRIENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosadiensäure", BLSNutrient.DOCOSADIENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosatriensäure", BLSNutrient.DOCOSATRIENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Docosatetraensäure", BLSNutrient.DOCOSATETRAENSAEURE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. mehrfach unges. Fettsäuren", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. kurzkettige Fettsäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("kurzkettige Fettsäuren", BLSNutrient.KURZKETTIGE_FETTSAEUREN);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. mittelkettige Fettsäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("mittelkettige Fettsäuren", BLSNutrient.MITTELKETTIGE_FETTSAEUREN);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. langkettige Fettsäuren", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("langkettige Fettsäuren", BLSNutrient.LANGKETTIGE_FETTSAEUREN);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycerin + Lipoide", BLSNutrient.GLYCERIN_UND_LIPOIDE);

        //Spezielle Kohlenhydrate
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Sorbit", BLSNutrient.SORBIT);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glucose", BLSNutrient.GLUCOSE_TRAUBENZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fructose", BLSNutrient.FRUCTOSE_FRUCHTZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Saccharose", BLSNutrient.SACCHAROSE_RUEBENZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lactose", BLSNutrient.LACTOSE_MILCHZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stärke", BLSNutrient.STAERKE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Gesamtzucker", BLSNutrient.ZUCKER_GESAMT);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Maltose", BLSNutrient.MALTOSE_MALZZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Galactose", BLSNutrient.GALACTOSE_SCHLEIMZUCKER);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Glycogen", BLSNutrient.GLYKOGEN_TIERISCHE_STAERKE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Pentosan", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Hexosan", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Cellulose", BLSNutrient.CELLULOSE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Polyuronsäure", BLSNutrient.POLY_URONSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mannit", BLSNutrient.MANNIT);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Xylit", BLSNutrient.XYLIT);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Zuckeralkohole", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Summe Zuckeralkohole", BLSNutrient.SUMME_ZUCKERALKOHOLE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Monosaccharide", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Monosaccharide", BLSNutrient.MONOSACCHARIDE_1_M);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Disaccharide", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Disaccharide", BLSNutrient.DISACCHARIDE_2_M);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Oligosaccharide resorb.", BLSNutrient.OLIGOSACCHARIDE_RESORBIERBAR_3_9_M);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Oligosaccharide n. resorb.", BLSNutrient.OLIGOSACCHARIDE_NICHT_RESORBIERBAR);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Polysaccharide", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Polysaccharide", BLSNutrient.POLYSACCHARIDE_MEHR_ALS_9_M);

        //Sonstiges
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe wasserl.", BLSNutrient.WASSERLOESLICHE_BALLASTSTOFFE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Ballaststoffe w.unlösl.", BLSNutrient.WASSERUNLOESLICHE_BALLASTSTOFFE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Lignin", BLSNutrient.LIGNIN);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Purinbasen-Stickstoff", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Kochsalz", BLSNutrient.GESAMT_KOCHSALZ);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Küchenabfälle", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. Eiweißstoffe", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("tierisches Eiweiß", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("pflanzliches Eiweiß", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Harnsäure", BLSNutrient.HARNSAEURE);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("sonst. organischen Säuren", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mol-Diff. Kationen-Anionen		mä", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stickstoffaktor", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fettsäurenanteil", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mineralstoffanteil", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("P/S Verhältnis", BLSNutrient.P_S_VERHAELTNIS);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Biolog. Wertigkeit", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("fruktosefreie Broteinheiten", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("durchschn. Verzehr", BLSNutrient.MITTLERE_PORTIONSGROESSE);

        //Allergene und Zusatzstoffe
        //WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fisch und Fischerzeugnisse", );

//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Nicotinamid");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("freies Folsäureäquivalent");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("freie Folsäure");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Selen");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. essent. Aminosäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. n. essent. Aminosäuren");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("C22:1 Fettsäure"); //TODO ?
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("C14:1 Fettsäure"); //TODO BLSNutrient.TETRADECENSAEURE?
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("C24:1 Fettsäure"); //TODO ?
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("C16:2 Fettsäure");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. gesättigte Fettsäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. einfach unges. Fettsäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. mehrfach unges. Fettsäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. kurzkettige Fettsäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. mittelkettige Fettsäuren");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. langkettige Fettsäuren");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Pentosan");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Hexosan");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. Zuckeralkohole");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. Monosaccharide");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. Disaccharide");
        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. Polysaccharide");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Purinbasen-Stickstoff");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Küchenabfälle");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. Eiweißstoffe");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("tierisches Eiweiß");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("pflanzliches Eiweiß");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("sonst. organischen Säuren");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Mol-Diff. Kationen-Anionen");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Stickstoffaktor");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Fettsäurenanteil");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Mineralstoffanteil");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Biolog. Wertigkeit");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("fruktosefreie Broteinheiten");
//        WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.add("Fisch und Fischerzeugnisse");
    }

    public static void main(String[] args) throws IOException {
        List<URL> foodURLs = getFoodURLs();
        String insertStatement = crawlFoodURLs(foodURLs);
        try (FileWriter fw = new FileWriter(System.getProperty("user.dir") + System.getProperty("file.separator") + "insert_foods.sql")) {//TODO command line parameter
            fw.write(insertStatement);
        }
    }

    private static List<URL> getFoodURLs() throws IOException {
        Document resultsDoc = Jsoup.connect(REQUEST_URL)
                .data("nameInput", "")
                .data("origin", "bls")
                .data("language", "de")
                .maxBodySize(0)
                .post();
        Iterator<Element> it = resultsDoc.getElementById("wrapper").getElementsByClass("list-group-item").iterator();
        List<URL> foodURLs = new ArrayList<>(15000); //BLS contains 14.814 foods (2017-05-12)
        while (it.hasNext()) {
            foodURLs.add(new URL(it.next().attr("href")));
        }
        LOGGER.info("Fetched " + foodURLs.size() + " food URLs.");
        return foodURLs;
    }

    private static String crawlFoodURLs(List<URL> foodURLs) throws IOException {
        LOGGER.info("Start crawling food URLs");
        StringBuilder insertStatement = new StringBuilder();
        insertStatement.append("INSERT INTO food(");
        //column list
        boolean first = true;
        for (BLSNutrient blsNutrient : BLSNutrient.values()) {
            if(first) {
                insertStatement.append(blsNutrient.getTableColumnName());
                first = false;
            } else {
                insertStatement.append(", ").append(blsNutrient.getTableColumnName());
            }
        }
        insertStatement.append(")\n VALUES ");
        //value lists
        LocalDateTime start = LocalDateTime.now();
        for(int i=0; i<foodURLs.size(); i++) {
            LOGGER.debug("Crawling food URL " + (i+1) + " of " + foodURLs.size());
            if(i%(foodURLs.size()/100) == 0) {
                String remainingTime = "calculating...";
                if(i != 0) {
                    Duration elapsedTime = Duration.between(start, LocalDateTime.now());
                    long remainingSeconds = elapsedTime.dividedBy(i).multipliedBy(foodURLs.size() - i).getSeconds();
                    long hours = remainingSeconds / (60*60); //seconds per hour
                    long minutes = ((remainingSeconds % (60*60)) / 60); //seconds per hour and seconds per minute
                    long secs = (remainingSeconds % 60); //seconds per minute
                    remainingTime = String.format("%02d:%02d:%02d", hours, minutes, secs);
                }
                LOGGER.info("Progress: " + i / (foodURLs.size() / 100) + "%, remaining Time: " + remainingTime);
            }
            if(i>0) {
                insertStatement.append(",\n ");
            }
            insertStatement.append(crawlFoodURL(foodURLs.get(i)));
        }
        insertStatement.append(";");
        LOGGER.info("Finished crawling food URLs");
        return insertStatement.toString();
    }

    private static String crawlFoodURL(URL foodURL) throws IOException {
        EnumMap<BLSNutrient, String> food = new EnumMap<>(BLSNutrient.class);
        String blsKey = foodURL.toString().replace("http://www.ernaehrung.de/lebensmittel/de/", "").substring(0, 7);
        food.put(BLSNutrient.BLS_SCHLUESSEL, blsKey);
        Document resultsDoc = Jsoup.connect(foodURL.toString()).get();
        String foodName = resultsDoc.getElementById("wrapper").getElementsByTag("h1").text();
        food.put(BLSNutrient.TEXT, foodName);
        List<Elements> tableRows = resultsDoc.getElementById("wrapper").getElementsByClass("table table-condensed table-striped table-bordered").stream().map(e -> e.getElementsByTag("tr")).collect(Collectors.toList());
        //parse data from tables
        for(Elements elements : tableRows) {
            for (Element tableRow : elements) {
                Elements tableData = tableRow.getElementsByTag("td");
                if (!tableData.isEmpty()) {
                    String websiteNutrientLongName = tableData.get(0).text();
                    String nutrientAmountStr = tableData.get(1).text().replace(",", ".");
                    String nutrientUnit = (tableData.size() >= 3 ? tableData.get(2).text() : null);
                    if (!WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.containsKey(websiteNutrientLongName) && !WEBSITE_IGNORED_NUTRIENT_LONG_NAMES.contains(websiteNutrientLongName)) {
                        LOGGER.debug("Unmapped nutrient \"" + websiteNutrientLongName + "\" " + nutrientAmountStr + (nutrientUnit != null && !nutrientUnit.isEmpty() ? " " + nutrientUnit : ""));
                    } else if (!nutrientAmountStr.isEmpty()) {
                        BigDecimal containedAmount = new BigDecimal(nutrientAmountStr);
                        if (nutrientUnit != null) {
                            switch (nutrientUnit) {
                                case "g":
                                    containedAmount = containedAmount.multiply(new BigDecimal(1000));
                                    break;
                                case "mg":
                                    //do nothing
                                    break;
                                case "µg":
                                    containedAmount = containedAmount.divide(new BigDecimal(1000), RoundingMode.HALF_UP);
                                    break;
                            }
                        }
                        food.put(WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.get(websiteNutrientLongName), containedAmount.toString());
                    }
                }
            }
        }

        //transform do SQL value list
        StringBuilder valueList = new StringBuilder().append("(");
        boolean first = true;
        for (BLSNutrient blsNutrient : BLSNutrient.values()) {
            if(!first) {
                valueList.append(", ");
            }
            first = false;
            valueList.append(food.getOrDefault(blsNutrient, "NULL"));
        }
        valueList.append(")");
        return valueList.toString();
    }
}
