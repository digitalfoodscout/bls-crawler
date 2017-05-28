package de.tu_dortmund.pgdfs.blscrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public class BLSCrawler {
    private static final String REQUEST_URL = "http://www.ernaehrung.de/lebensmittel/suche/";
    private static final Logger LOGGER = LoggerFactory.getLogger(BLSCrawler.class);
    private static final String CREATE_TABLE_STATEMENT = "USE foodscout;\n" +
            "CREATE OR REPLACE TABLE food (\n" +
            "SBLS CHAR(7) NOT NULL PRIMARY KEY COMMENT 'BLS-Schlüssel' CHECK(SBLS REGEXP '^[a-zA-Z][0-9][0-9a-zA-Z]{2}[0-9]{3}$'),\n" +
            "ST VARCHAR(60) NOT NULL COMMENT 'Text',\n" +
            "STE VARCHAR(60) DEFAULT NULL COMMENT 'Text englisch',\n" +
            "GCAL DOUBLE DEFAULT NULL COMMENT 'Energie (Kilokalorien) in kcal/100 g',\n" +
            "GJ DOUBLE DEFAULT NULL COMMENT 'Energie (Kilojoule) in kJ/100 g',\n" +
            "GCALZB DOUBLE DEFAULT NULL COMMENT 'Energie inkl. Energie aus Ballaststoffen (Kilokalorien) in kcal/100 g',\n" +
            "GJZB DOUBLE DEFAULT NULL COMMENT 'Energie inkl. Energie aus Ballaststoffen (Kilojoule) in kJ/100 g',\n" +
            "ZW DOUBLE DEFAULT NULL COMMENT 'Wasser in mg/100 g',\n" +
            "ZE DOUBLE DEFAULT NULL COMMENT 'Eiweiß (Protein) in mg/100 g',\n" +
            "ZF DOUBLE DEFAULT NULL COMMENT 'Fett in mg/100 g',\n" +
            "ZK DOUBLE DEFAULT NULL COMMENT 'Kohlenhydrate, resorbierbar in mg/100 g',\n" +
            "ZB DOUBLE DEFAULT NULL COMMENT 'Ballaststoffe in mg/100 g',\n" +
            "ZM DOUBLE DEFAULT NULL COMMENT 'Mineralstoffe (Rohasche) in mg/100 g',\n" +
            "ZO DOUBLE DEFAULT NULL COMMENT 'Organische Säuren in mg/100 g',\n" +
            "ZA DOUBLE DEFAULT NULL COMMENT 'Alkohol (Ethanol) in mg/100 g',\n" +
            "VA DOUBLE DEFAULT NULL COMMENT 'Vitamin A-Retinoläquivalent in mg/100 g',\n" +
            "VAR DOUBLE DEFAULT NULL COMMENT 'Vitamin A-Retinol in mg/100 g',\n" +
            "VAC DOUBLE DEFAULT NULL COMMENT 'Vitamin A-Beta-Carotin in mg/100 g',\n" +
            "VD DOUBLE DEFAULT NULL COMMENT 'Vitamin D-Calciferole in mg/100 g',\n" +
            "VE DOUBLE DEFAULT NULL COMMENT 'Vitamin E-Alpha-Tocopheroläquivalent in mg/100 g',\n" +
            "VEAT DOUBLE DEFAULT NULL COMMENT 'Vitamin E-Alpha-Tocopherol in mg/100 g',\n" +
            "VK DOUBLE DEFAULT NULL COMMENT 'Vitamin K-Phyllochinon in mg/100 g',\n" +
            "VB1 DOUBLE DEFAULT NULL COMMENT 'Vitamin B1-Thiamin in mg/100 g',\n" +
            "VB2 DOUBLE DEFAULT NULL COMMENT 'Vitamin B2-Riboflavin in mg/100 g',\n" +
            "VB3 DOUBLE DEFAULT NULL COMMENT 'Vitamin B3-Niacin, Nicotinsäure in mg/100 g',\n" +
            "VB3A DOUBLE DEFAULT NULL COMMENT 'Vitamin B3-Niacinäquivalent in mg/100 g',\n" +
            "VB5 DOUBLE DEFAULT NULL COMMENT 'Vitamin B5-Pantothensäure in mg/100 g',\n" +
            "VB6 DOUBLE DEFAULT NULL COMMENT 'Vitamin B6-Pyridoxin in mg/100 g',\n" +
            "VB7 DOUBLE DEFAULT NULL COMMENT 'Vitamin B7-Biotin (Vitamin H) in mg/100 g',\n" +
            "VB9G DOUBLE DEFAULT NULL COMMENT 'Vitamin B9-gesamte Folsäure in mg/100 g',\n" +
            "VB12 DOUBLE DEFAULT NULL COMMENT 'Vitamin B12-Cobalamin in mg/100 g',\n" +
            "VC DOUBLE DEFAULT NULL COMMENT 'Vitamin C-Ascorbinsäure in mg/100 g',\n" +
            "MNA DOUBLE DEFAULT NULL COMMENT 'Natrium in mg/100 g',\n" +
            "MK DOUBLE DEFAULT NULL COMMENT 'Kalium in mg/100 g',\n" +
            "MCA DOUBLE DEFAULT NULL COMMENT 'Calcium in mg/100 g',\n" +
            "MMG DOUBLE DEFAULT NULL COMMENT 'Magnesium in mg/100 g',\n" +
            "MP DOUBLE DEFAULT NULL COMMENT 'Phosphor in mg/100 g',\n" +
            "MS DOUBLE DEFAULT NULL COMMENT 'Schwefel in mg/100 g',\n" +
            "MCL DOUBLE DEFAULT NULL COMMENT 'Chlorid in mg/100 g',\n" +
            "MFE DOUBLE DEFAULT NULL COMMENT 'Eisen in mg/100 g',\n" +
            "MZN DOUBLE DEFAULT NULL COMMENT 'Zink in mg/100 g',\n" +
            "MCU DOUBLE DEFAULT NULL COMMENT 'Kupfer in mg/100 g',\n" +
            "MMN DOUBLE DEFAULT NULL COMMENT 'Mangan in mg/100 g',\n" +
            "MF DOUBLE DEFAULT NULL COMMENT 'Fluorid in mg/100 g',\n" +
            "MJ DOUBLE DEFAULT NULL COMMENT 'Iodid in mg/100 g',\n" +
            "KAM DOUBLE DEFAULT NULL COMMENT 'Mannit in mg/100 g',\n" +
            "KAS DOUBLE DEFAULT NULL COMMENT 'Sorbit in mg/100 g',\n" +
            "KAX DOUBLE DEFAULT NULL COMMENT 'Xylit in mg/100 g',\n" +
            "KA DOUBLE DEFAULT NULL COMMENT 'Summe Zuckeralkohole in mg/100 g',\n" +
            "KMT DOUBLE DEFAULT NULL COMMENT 'Glucose (Traubenzucker) in mg/100 g',\n" +
            "KMF DOUBLE DEFAULT NULL COMMENT 'Fructose (Fruchtzucker) in mg/100 g',\n" +
            "KMG DOUBLE DEFAULT NULL COMMENT 'Galactose (Schleimzucker) in mg/100 g',\n" +
            "KM DOUBLE DEFAULT NULL COMMENT 'Monosaccharide (1 M) in mg/100 g',\n" +
            "KDS DOUBLE DEFAULT NULL COMMENT 'Saccharose (Rübenzucker) in mg/100 g',\n" +
            "KDM DOUBLE DEFAULT NULL COMMENT 'Maltose (Malzzucker) in mg/100 g',\n" +
            "KDL DOUBLE DEFAULT NULL COMMENT 'Lactose (Milchzucker) in mg/100 g',\n" +
            "KD DOUBLE DEFAULT NULL COMMENT 'Disaccharide (2 M) in mg/100 g',\n" +
            "KMD DOUBLE DEFAULT NULL COMMENT 'Zucker (gesamt) in mg/100 g',\n" +
            "KPOR DOUBLE DEFAULT NULL COMMENT 'Oligosaccharide, resorbierbar (3 - 9 M) in mg/100 g',\n" +
            "KPON DOUBLE DEFAULT NULL COMMENT 'Oligosaccharide, nicht resorbierbar in mg/100 g',\n" +
            "KPG DOUBLE DEFAULT NULL COMMENT 'Glykogen (tierische Stärke) in mg/100 g',\n" +
            "KPS DOUBLE DEFAULT NULL COMMENT 'Stärke in mg/100 g',\n" +
            "KP DOUBLE DEFAULT NULL COMMENT 'Polysaccharide (> 9 M) in mg/100 g',\n" +
            "KBP DOUBLE DEFAULT NULL COMMENT 'Poly-Pentosen in mg/100 g',\n" +
            "KBH DOUBLE DEFAULT NULL COMMENT 'Poly-Hexosen in mg/100 g',\n" +
            "KBU DOUBLE DEFAULT NULL COMMENT 'Poly-Uronsäure in mg/100 g',\n" +
            "KBC DOUBLE DEFAULT NULL COMMENT 'Cellulose in mg/100 g',\n" +
            "KBL DOUBLE DEFAULT NULL COMMENT 'Lignin in mg/100 g',\n" +
            "KBW DOUBLE DEFAULT NULL COMMENT 'Wasserlösliche Ballaststoffe in mg/100 g',\n" +
            "KBN DOUBLE DEFAULT NULL COMMENT 'Wasserunlösliche Ballaststoffe in mg/100 g',\n" +
            "EILE DOUBLE DEFAULT NULL COMMENT 'Isoleucin in mg/100 g',\n" +
            "ELEU DOUBLE DEFAULT NULL COMMENT 'Leucin in mg/100 g',\n" +
            "ELYS DOUBLE DEFAULT NULL COMMENT 'Lysin in mg/100 g',\n" +
            "EMET DOUBLE DEFAULT NULL COMMENT 'Methionin in mg/100 g',\n" +
            "ECYS DOUBLE DEFAULT NULL COMMENT 'Cystein in mg/100 g',\n" +
            "EPHE DOUBLE DEFAULT NULL COMMENT 'Phenylalanin in mg/100 g',\n" +
            "ETYR DOUBLE DEFAULT NULL COMMENT 'Tyrosin in mg/100 g',\n" +
            "ETHR DOUBLE DEFAULT NULL COMMENT 'Threonin in mg/100 g',\n" +
            "ETRP DOUBLE DEFAULT NULL COMMENT 'Tryptophan in mg/100 g',\n" +
            "EVAL DOUBLE DEFAULT NULL COMMENT 'Valin in mg/100 g',\n" +
            "EARG DOUBLE DEFAULT NULL COMMENT 'Arginin in mg/100 g',\n" +
            "EHIS DOUBLE DEFAULT NULL COMMENT 'Histidin in mg/100 g',\n" +
            "EEA DOUBLE DEFAULT NULL COMMENT 'Essentielle Aminosäuren in mg/100 g',\n" +
            "EALA DOUBLE DEFAULT NULL COMMENT 'Alanin in mg/100 g',\n" +
            "EASP DOUBLE DEFAULT NULL COMMENT 'Asparaginsäure in mg/100 g',\n" +
            "EGLU DOUBLE DEFAULT NULL COMMENT 'Glutaminsäure in mg/100 g',\n" +
            "EGLY DOUBLE DEFAULT NULL COMMENT 'Glycin in mg/100 g',\n" +
            "EPRO DOUBLE DEFAULT NULL COMMENT 'Prolin in mg/100 g',\n" +
            "ESER DOUBLE DEFAULT NULL COMMENT 'Serin in mg/100 g',\n" +
            "ENA DOUBLE DEFAULT NULL COMMENT 'Nichtessentielle Aminosäuren in mg/100 g',\n" +
            "EH DOUBLE DEFAULT NULL COMMENT 'Harnsäure in mg/100 g',\n" +
            "EP DOUBLE DEFAULT NULL COMMENT 'Purin in mg/100 g',\n" +
            "F40 DOUBLE DEFAULT NULL COMMENT 'Butansäure/Buttersäure in mg/100 g',\n" +
            "F60 DOUBLE DEFAULT NULL COMMENT 'Hexansäure/Capronsäure in mg/100 g',\n" +
            "F80 DOUBLE DEFAULT NULL COMMENT 'Octansäure/Caprylsäure in mg/100 g',\n" +
            "F100 DOUBLE DEFAULT NULL COMMENT 'Decansäure/Caprinsäure in mg/100 g',\n" +
            "F120 DOUBLE DEFAULT NULL COMMENT 'Dodecansäure/Laurinsäure in mg/100 g',\n" +
            "F140 DOUBLE DEFAULT NULL COMMENT 'Tetradecansäure/Myristinsäure in mg/100 g',\n" +
            "F150 DOUBLE DEFAULT NULL COMMENT 'Pentadecansäure in mg/100 g',\n" +
            "F160 DOUBLE DEFAULT NULL COMMENT 'Hexadecansäure/Palmitinsäure in mg/100 g',\n" +
            "F170 DOUBLE DEFAULT NULL COMMENT 'Heptadecansäure in mg/100 g',\n" +
            "F180 DOUBLE DEFAULT NULL COMMENT 'Octadecansäure/Stearinsäure in mg/100 g',\n" +
            "F200 DOUBLE DEFAULT NULL COMMENT 'Eicosansäure/Arachinsäure in mg/100 g',\n" +
            "F220 DOUBLE DEFAULT NULL COMMENT 'Docosansäure/Behensäure in mg/100 g',\n" +
            "F240 DOUBLE DEFAULT NULL COMMENT 'Tetracosansäure/Lignocerinsäure in mg/100 g',\n" +
            "FS DOUBLE DEFAULT NULL COMMENT 'Gesättigte Fettsäuren in mg/100 g',\n" +
            "F141 DOUBLE DEFAULT NULL COMMENT 'Tetradecensäure in mg/100 g',\n" +
            "F151 DOUBLE DEFAULT NULL COMMENT 'Pentadecensäure in mg/100 g',\n" +
            "F161 DOUBLE DEFAULT NULL COMMENT 'Hexadecensäure/Palmitoleinsäure in mg/100 g',\n" +
            "F171 DOUBLE DEFAULT NULL COMMENT 'Heptadecensäure in mg/100 g',\n" +
            "F181 DOUBLE DEFAULT NULL COMMENT 'Octadecensäure/Ölsäure in mg/100 g',\n" +
            "F201 DOUBLE DEFAULT NULL COMMENT 'Eicosensäure in mg/100 g',\n" +
            "F221 DOUBLE DEFAULT NULL COMMENT 'Docosensäure/Erucasäure in mg/100 g',\n" +
            "F241 DOUBLE DEFAULT NULL COMMENT 'Tetracosensäure/Nervonsäure in mg/100 g',\n" +
            "FU DOUBLE DEFAULT NULL COMMENT 'Einfach ungesättigte Fettsäuren in mg/100 g',\n" +
            "F162 DOUBLE DEFAULT NULL COMMENT 'Hexadecadiensäure in mg/100 g',\n" +
            "F164 DOUBLE DEFAULT NULL COMMENT 'Hexadecatetraensäure in mg/100 g',\n" +
            "F182 DOUBLE DEFAULT NULL COMMENT 'Octadecadiensäure/Linolsäure in mg/100 g',\n" +
            "F183 DOUBLE DEFAULT NULL COMMENT 'Octadecatriensäure/Linolensäure in mg/100 g',\n" +
            "F184 DOUBLE DEFAULT NULL COMMENT 'Octadecatetraensäure/Stearidonsäure in mg/100 g',\n" +
            "F193 DOUBLE DEFAULT NULL COMMENT 'Nonadecatriensäure in mg/100 g',\n" +
            "F202 DOUBLE DEFAULT NULL COMMENT 'Eicosadiensäure in mg/100 g',\n" +
            "F203 DOUBLE DEFAULT NULL COMMENT 'Eicosatriensäure in mg/100 g',\n" +
            "F204 DOUBLE DEFAULT NULL COMMENT 'Eicosatetraensäure/Arachidonsäure in mg/100 g',\n" +
            "F205 DOUBLE DEFAULT NULL COMMENT 'Eicosapentaensäure in mg/100 g',\n" +
            "F222 DOUBLE DEFAULT NULL COMMENT 'Docosadiensäure in mg/100 g',\n" +
            "F223 DOUBLE DEFAULT NULL COMMENT 'Docosatriensäure in mg/100 g',\n" +
            "F224 DOUBLE DEFAULT NULL COMMENT 'Docosatetraensäure in mg/100 g',\n" +
            "F225 DOUBLE DEFAULT NULL COMMENT 'Docosapentaensäure in mg/100 g',\n" +
            "F226 DOUBLE DEFAULT NULL COMMENT 'Docosahexaensäure in mg/100 g',\n" +
            "FP DOUBLE DEFAULT NULL COMMENT 'Mehrfach ungesättigte Fettsäuren in mg/100 g',\n" +
            "FK DOUBLE DEFAULT NULL COMMENT 'Kurzkettige Fettsäuren in mg/100 g',\n" +
            "FM DOUBLE DEFAULT NULL COMMENT 'Mittelkettige Fettsäuren in mg/100 g',\n" +
            "FL DOUBLE DEFAULT NULL COMMENT 'Langkettige Fettsäuren in mg/100 g',\n" +
            "FO3 DOUBLE DEFAULT NULL COMMENT 'Omega-3-Fettsäuren in mg/100 g',\n" +
            "FO6 DOUBLE DEFAULT NULL COMMENT 'Omega-6-Fettsäuren in mg/100 g',\n" +
            "FG DOUBLE DEFAULT NULL COMMENT 'Glycerin und Lipoide in mg/100 g',\n" +
            "FC DOUBLE DEFAULT NULL COMMENT 'Cholesterin in mg/100 g',\n" +
            "GFPS DOUBLE DEFAULT NULL COMMENT 'P/S Verhältnis in ',\n" +
            "GKB DOUBLE DEFAULT NULL COMMENT 'Broteinheiten in BE',\n" +
            "GMKO DOUBLE DEFAULT NULL COMMENT 'Gesamt-Kochsalz in mg/100 g',\n" +
            "GP DOUBLE DEFAULT NULL COMMENT 'Mittlere Portionsgröße in g/Port',\n" +
            "INDEX btree_food_st USING BTREE (ST)\n" +
            ")\n" +
            "CHARACTER SET 'utf8',\n" +
            "COMMENT 'version as of " + LocalDateTime.now().toString() + "';";
    private static final HashMap<String, BLSNutrient> WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP = new HashMap<>();
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
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:1 Fettsäure", BLSNutrient.DOCOSENSAEURE_ERUCASAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C14:1 Fettsäure", BLSNutrient.TETRADECENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C24:1 Fettsäure", BLSNutrient.TETRACOSENSAEURE_NERVONSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolsäure", BLSNutrient.OCTADECADIENSAEURE_LINOLSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Linolensäure", BLSNutrient.OCTADECATRIENSAEURE_LINOLENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Arachidonsäure", BLSNutrient.EICOSATETRAENSAEURE_ARACHIDONSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C18:4 Fettsäure", BLSNutrient.OCTADECATETRAENSAEURE_STEARIDONSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C20:5 N-3 Fettsäure", BLSNutrient.EICOSAPENTAENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:5 N-3 Fettsäure", BLSNutrient.DOCOSAPENTAENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C22:6 N-3 Fettsäure", BLSNutrient.DOCOSAHEXAENSAEURE);
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("C16:2 Fettsäure", BLSNutrient.HEXADECADIENSAEURE);
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
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mol-Diff. Kationen-Anionen		Unit mä?", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Stickstoffaktor", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Fettsäurenanteil", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Mineralstoffanteil", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("P/S Verhältnis", BLSNutrient.P_S_VERHAELTNIS);
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("Biolog. Wertigkeit", );
//        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("fruktosefreie Broteinheiten", );
        WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.put("durchschn. Verzehr", BLSNutrient.MITTLERE_PORTIONSGROESSE);
    }

    public static void main(String[] args) throws IOException {
        if(args.length != 1) {
            throw new IllegalArgumentException("Expected exactly one argument: path to file to save generated SQL to");
        } else if(new File(args[0]).exists()) {
            throw new IllegalArgumentException("Output file \"" + args[0] + "\" already exists! Delete the file and restart.");
        }

        List<URL> foodURLs = getFoodURLs();
        String insertStatement = crawlFoodURLs(foodURLs);
        try (FileWriter fw = new FileWriter(args[0])) {
            fw.write(CREATE_TABLE_STATEMENT + "\n\n" + insertStatement);
            LOGGER.info("Generated SQL saved to " + new File(args[0]).getAbsolutePath());
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

        //build tasks list
        List<Callable<String>> tasks = new ArrayList<>();
        for (final URL foodURL : foodURLs) {
            Callable<String> c = () -> crawlFoodURL(foodURL);
            tasks.add(c);
        }

        //execute
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        try {
            LOGGER.info("Start crawling tasks");
            LocalDateTime start = LocalDateTime.now();
            List<Future<String>> results = new LinkedList<>();
            for(Callable<String> task : tasks) {
                results.add(executorService.submit(task));
            }
            //print progress during crawling
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if(!Thread.currentThread().isInterrupted()) {
                    String remainingTime = "calculating...";
                    int processed = tasks.size() - results.size();
                    if (processed > 0) {
                        Duration elapsedTime = Duration.between(start, LocalDateTime.now());
                        Duration estimatedRemainingTime = elapsedTime.dividedBy(processed).multipliedBy(results.size());
                        remainingTime = prettyPrintDuration(estimatedRemainingTime);
                    }
                    LOGGER.info("Progress: " + processed / (foodURLs.size() / 100) + "%, remaining time: " + remainingTime);
                }
            }, 0, 3, TimeUnit.SECONDS);
            first = true;
            while(!results.isEmpty()) {
                Iterator<Future<String>> it = results.iterator();
                while(it.hasNext()) {
                    Future<String> future = it.next();
                    try {
                        String result = future.get(1, TimeUnit.SECONDS);
                        if (!first) {
                            insertStatement.append(",\n ");
                        }
                        insertStatement.append(result);
                        first = false;
                        it.remove();
                    } catch (TimeoutException e) {
                        //swallow
                    }
                }
            }
            LOGGER.info("Completed crawling in " + prettyPrintDuration(Duration.between(start, LocalDateTime.now())));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            scheduledExecutorService.shutdownNow();
            executorService.shutdownNow();
        }
        insertStatement.append(";");
        LOGGER.info("Finished crawling food URLs.");
        return insertStatement.toString();
    }

    private static String crawlFoodURL(URL foodURL) throws IOException {
        EnumMap<BLSNutrient, String> food = new EnumMap<>(BLSNutrient.class);
        String blsKey = foodURL.toString().replace("http://www.ernaehrung.de/lebensmittel/de/", "").substring(0, 7);
        food.put(BLSNutrient.BLS_SCHLUESSEL, blsKey);
        Document resultsDoc = Jsoup.connect(foodURL.toString()).get();
        String foodName = resultsDoc.getElementById("wrapper").getElementsByTag("h1").text();
        food.put(BLSNutrient.TEXT, foodName);
        List<Elements> tables = resultsDoc.getElementById("wrapper").getElementsByClass("table table-condensed table-striped table-bordered").subList(0, 7).stream().map(e -> e.getElementsByTag("tr")).collect(Collectors.toList());
        //parse data from tables
        for(Elements table : tables) {
            for (Element tableRow : table) {
                Elements tableData = tableRow.getElementsByTag("td");
                if (!tableData.isEmpty()) { //to filter out table rows which only contain th-elements
                    String websiteNutrientLongName = tableData.get(0).text();
                    String nutrientAmountStr = tableData.get(1).text().replace(",", ".");
                    String nutrientUnit = (tableData.size() >= 3 ? tableData.get(2).text() : null);
                    if (!WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.containsKey(websiteNutrientLongName)) {
                        if(!nutrientAmountStr.isEmpty()) {
                            LOGGER.warn("Unmapped nutrient in " + foodName + ": \"" + websiteNutrientLongName + "\" " + nutrientAmountStr + (nutrientUnit != null && !nutrientUnit.isEmpty() ? " " + nutrientUnit : ""));
                        } else {
                            LOGGER.debug("Unmapped nutrient in " + foodName + ": \"" + websiteNutrientLongName + "\" " + nutrientAmountStr + (nutrientUnit != null && !nutrientUnit.isEmpty() ? " " + nutrientUnit : ""));
                        }
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
                        food.put(WEBSITE_NUTRIENT_LONG_NAME_TO_BLS_NUTRIENT_MAP.get(websiteNutrientLongName), Double.toString(containedAmount.doubleValue()));
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
            if(blsNutrient.equals(BLSNutrient.BLS_SCHLUESSEL) || blsNutrient.equals(BLSNutrient.TEXT) || blsNutrient.equals(BLSNutrient.TEXT_ENGLISCH))
            {
                if(food.containsKey(blsNutrient)) {
                    valueList.append("'").append(food.get(blsNutrient)).append("'");
                } else {
                    valueList.append("NULL");
                }
            } else {
                valueList.append(food.getOrDefault(blsNutrient, "NULL"));
            }
        }
        valueList.append(")");
        return valueList.toString();
    }

    private static String prettyPrintDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long hours = seconds / (60*60); //seconds per hour
        long minutes = ((seconds % (60*60)) / 60); //seconds per hour and seconds per minute
        long secs = (seconds % 60); //seconds per minute
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
