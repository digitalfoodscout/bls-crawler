package de.tu_dortmund.pgdfs.blscrawler;

import java.util.HashMap;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public enum BLSNutrient {
    //Schlüsselwerte
    BLS_SCHLUESSEL("BLS-Schlüssel", "SBLS", ""),
    TEXT("Text", "ST", ""),
    TEXT_ENGLISCH("Text englisch", "STE", ""),

    //Zusammensetzung
    ENERGIE_KILOKALORIEN("Energie (Kilokalorien)", "GCAL", "kcal/100 g"),
    ENERGIE_KILOJOULE("Energie (Kilojoule)", "GJ", "kJ/100 g"),
    ENERGIE_INKL_BALLASTSTOFFE_KILOKALORIEN("Energie inkl. Energie aus Ballaststoffen (Kilokalorien)", "GCALZB", "kcal/100 g"), //TODO no usage
    ENERGIE_INKL_BALLASTSTOFFE_KILOJOULE("Energie inkl. Energie aus Ballaststoffen (Kilojoule)", "GJZB", "kJ/100 g"), //TODO no usage
    WASSER("Wasser", "ZW"),
    EIWEISS_PROTEIN("Eiweiß (Protein)", "ZE"),
    FETT("Fett", "ZF"),
    KOHLENHYDRATE_RESORBIERBAR("Kohlenhydrate, resorbierbar", "ZK"),
    BALLASTSTOFFE("Ballaststoffe", "ZB"),
    MINERALSTOFFE_ROHASCHE("Mineralstoffe (Rohasche)", "ZM"),
    ORGANISCHE_SAEUREN("Organische Säuren", "ZO"),  //TODO no usage
    ALKOHOL_ETHANOL("Alkohol (Ethanol)", "ZA"),

    //Vitamine
    VITAMIN_A_RETINOLAEQUIVALENT("Vitamin A-Retinoläquivalent", "VA"),
    VITAMIN_A_RETINOL("Vitamin A-Retinol", "VAR"),
    VITAMIN_A_BETA_CAROTIN("Vitamin A-Beta-Carotin", "VAC"),
    VITAMIN_D_CALCIFEROLE("Vitamin D-Calciferole", "VD"),
    VITAMIN_E_ALPHA_TOCOPHEROLAEQUIVALENT("Vitamin E-Alpha-Tocopheroläquivalent", "VE"),
    VITAMIN_E_ALPHA_TOCOPHEROL("Vitamin E-Alpha-Tocopherol", "VEAT"),
    VITAMIN_K_PHYLLOCHINON("Vitamin K-Phyllochinon", "VK"),
    VITAMIN_B1_THIAMIN("Vitamin B1-Thiamin", "VB1"),
    VITAMIN_B2_RIBOFLAVIN("Vitamin B2-Riboflavin", "VB2"),
    VITAMIN_B3_NIACIN_NICOTINSAEURE("Vitamin B3-Niacin, Nicotinsäure", "VB3"), //TODO no usage
    VITAMIN_B3_NIACINAEQUIVALENT("Vitamin B3-Niacinäquivalent", "VB3A"),
    VITAMIN_B5_PANTOTHENSAEURE("Vitamin B5-Pantothensäure", "VB5"),
    VITAMIN_B6_PYRIDOXIN("Vitamin B6-Pyridoxin", "VB6"),
    VITAMIN_B7_BIOTIN_VITAMIN_H("Vitamin B7-Biotin (Vitamin H)", "VB7"),
    VITAMIN_B9_GESAMTE_FOLSAEURE("Vitamin B9-gesamte Folsäure", "VB9G"),
    VITAMIN_B12_COBALAMIN("Vitamin B12-Cobalamin", "VB12"),
    VITAMIN_C_ASCORBINSAEURE("Vitamin C-Ascorbinsäure", "VC"),

    //Mineralstoffe
    NATRIUM("Natrium", "MNA"),
    KALIUM("Kalium", "MK"),
    CALCIUM("Calcium", "MCA"),
    MAGNESIUM("Magnesium", "MMG"),
    PHOSPHOR("Phosphor", "MP"),
    SCHWEFEL("Schwefel", "MS"),
    CHLORID("Chlorid", "MCL"),

    //Spurenelemente
    EISEN("Eisen", "MFE"),
    ZINK("Zink", "MZN"),
    KUPFER("Kupfer", "MCU"),
    MANGAN("Mangan", "MMN"),
    FLUORID("Fluorid", "MF"),
    IODID("Iodid", "MJ"),

    //Kohlenhydratzusammensetzung
    MANNIT("Mannit", "KAM"),
    SORBIT("Sorbit", "KAS"),
    XYLIT("Xylit", "KAX"),
    SUMME_ZUCKERALKOHOLE("Summe Zuckeralkohole", "KA"),
    GLUCOSE_TRAUBENZUCKER("Glucose (Traubenzucker)", "KMT"),
    FRUCTOSE_FRUCHTZUCKER("Fructose (Fruchtzucker)", "KMF"),
    GALACTOSE_SCHLEIMZUCKER("Galactose (Schleimzucker)", "KMG"),
    MONOSACCHARIDE_1_M("Monosaccharide (1 M)", "KM"),
    SACCHAROSE_RUEBENZUCKER("Saccharose (Rübenzucker)", "KDS"),
    MALTOSE_MALZZUCKER("Maltose (Malzzucker)", "KDM"),
    LACTOSE_MILCHZUCKER("Lactose (Milchzucker)", "KDL"),
    DISACCHARIDE_2_M("Disaccharide (2 M)", "KD"),
    ZUCKER_GESAMT("Zucker (gesamt)", "KMD"),
    OLIGOSACCHARIDE_RESORBIERBAR_3_9_M("Oligosaccharide, resorbierbar (3 - 9 M)", "KPOR"),
    OLIGOSACCHARIDE_NICHT_RESORBIERBAR("Oligosaccharide, nicht resorbierbar", "KPON"),
    GLYKOGEN_TIERISCHE_STAERKE("Glykogen (tierische Stärke)", "KPG"),
    STAERKE("Stärke", "KPS"),
    POLYSACCHARIDE_MEHR_ALS_9_M("Polysaccharide (> 9 M)", "KP"),

    //Ballaststoffzusammensetzung
    POLY_PENTOSEN("Poly-Pentosen", "KBP"), //TODO no usage
    POLY_HEXOSEN("Poly-Hexosen", "KBH"), //TODO no usage
    POLY_URONSAEURE("Poly-Uronsäure", "KBU"),
    CELLULOSE("Cellulose", "KBC"),
    LIGNIN("Lignin", "KBL"),
    WASSERLOESLICHE_BALLASTSTOFFE("Wasserlösliche Ballaststoffe", "KBW"),
    WASSERUNLOESLICHE_BALLASTSTOFFE("Wasserunlösliche Ballaststoffe", "KBN"),

    //Aminosäuren (Eiweißzusammensetzung)
    ISOLEUCIN("Isoleucin", "EILE"),
    LEUCIN("Leucin", "ELEU"),
    LYSIN("Lysin", "ELYS"),
    METHIONIN("Methionin", "EMET"),
    CYSTEIN("Cystein", "ECYS"),
    PHENYLALANIN("Phenylalanin", "EPHE"),
    TYROSIN("Tyrosin", "ETYR"),
    THREONIN("Threonin", "ETHR"),
    TRYPTOPHAN("Tryptophan", "ETRP"),
    VALIN("Valin", "EVAL"),
    ARGININ("Arginin", "EARG"),
    HISTIDIN("Histidin", "EHIS"),
    ESSENTIELLE_AMINOSAEUREN("Essentielle Aminosäuren", "EEA"),
    ALANIN("Alanin", "EALA"),
    ASPARAGINSAEURE("Asparaginsäure", "EASP"),
    GLUTAMINSAEURE("Glutaminsäure", "EGLU"),
    GLYCIN("Glycin", "EGLY"),
    PROLIN("Prolin", "EPRO"),
    SERIN("Serin", "ESER"),
    NICHTESSENTIELLE_AMINOSAEUREN("Nichtessentielle Aminosäuren", "ENA"),
    HARNSAEURE("Harnsäure", "EH"),
    PURIN("Purin", "EP"), //TODO no usage

    //Fettzusammensetzung (Fettsäuren)
    BUTANSAEURE_BUTTERSAEURE("Butansäure/Buttersäure", "F40"),
    HEXANSAEURE_CAPRONSAEURE("Hexansäure/Capronsäure", "F60"),
    OCTANSAEURE_CAPRYLSAEURE("Octansäure/Caprylsäure", "F80"),
    DECANSAEURE_CAPRINSAEURE("Decansäure/Caprinsäure", "F100"),
    DODECANSAEURE_LAURINSAEURE("Dodecansäure/Laurinsäure", "F120"),
    TETRADECANSAEURE_MYRISTINSAEURE("Tetradecansäure/Myristinsäure", "F140"),
    PENTADECANSAEURE("Pentadecansäure", "F150"),
    HEXADECANSAEURE_PALMITINSAEURE("Hexadecansäure/Palmitinsäure", "F160"),
    HEPTADECANSAEURE("Heptadecansäure", "F170"),
    OCTADECANSAEURE_STEARINSAEURE("Octadecansäure/Stearinsäure", "F180"),
    EICOSANSAEURE_ARACHINSAEURE("Eicosansäure/Arachinsäure", "F200"),
    DOCOSANSAEURE_BEHENSAEURE("Docosansäure/Behensäure", "F220"),
    TETRACOSANSAEURE_LIGNOCERINSAEURE("Tetracosansäure/Lignocerinsäure", "F240"),
    GESAETTIGTE_FETTSAEUREN("Gesättigte Fettsäuren", "FS"),
    TETRADECENSAEURE("Tetradecensäure", "F141"),
    PENTADECENSAEURE("Pentadecensäure", "F151"), //TODO no usage
    HEXADECENSAEURE_PALMITOLEINSAEURE("Hexadecensäure/Palmitoleinsäure", "F161"),
    HEPTADECENSAEURE("Heptadecensäure", "F171"), //TODO no usage
    OCTADECENSAEURE_OELSAEURE("Octadecensäure/Ölsäure", "F181"),
    EICOSENSAEURE("Eicosensäure", "F201"),
    DOCOSENSAEURE_ERUCASAEURE("Docosensäure/Erucasäure", "F221"),
    TETRACOSENSAEURE_NERVONSAEURE("Tetracosensäure/Nervonsäure", "F241"),
    EINFACH_UNGESAETTIGTE_FETTSAEUREN("Einfach ungesättigte Fettsäuren", "FU"),
    HEXADECADIENSAEURE("Hexadecadiensäure", "F162"),
    HEXADECATETRAENSAEURE("Hexadecatetraensäure", "F164"), //TODO no usage
    OCTADECADIENSAEURE_LINOLSAEURE("Octadecadiensäure/Linolsäure", "F182"),
    OCTADECATRIENSAEURE_LINOLENSAEURE("Octadecatriensäure/Linolensäure", "F183"),
    OCTADECATETRAENSAEURE_STEARIDONSAEURE("Octadecatetraensäure/Stearidonsäure", "F184"),
    NONADECATRIENSAEURE("Nonadecatriensäure", "F193"),
    EICOSADIENSAEURE("Eicosadiensäure", "F202"),
    EICOSATRIENSAEURE("Eicosatriensäure", "F203"),
    EICOSATETRAENSAEURE_ARACHIDONSAEURE("Eicosatetraensäure/Arachidonsäure", "F204"),
    EICOSAPENTAENSAEURE("Eicosapentaensäure", "F205"),
    DOCOSADIENSAEURE("Docosadiensäure", "F222"),
    DOCOSATRIENSAEURE("Docosatriensäure", "F223"),
    DOCOSATETRAENSAEURE("Docosatetraensäure", "F224"),
    DOCOSAPENTAENSAEURE("Docosapentaensäure", "F225"),
    DOCOSAHEXAENSAEURE("Docosahexaensäure", "F226"),
    MEHRFACH_UNGESAETTIGTE_FETTSAEUREN("Mehrfach ungesättigte Fettsäuren", "FP"),
    KURZKETTIGE_FETTSAEUREN("Kurzkettige Fettsäuren", "FK"),
    MITTELKETTIGE_FETTSAEUREN("Mittelkettige Fettsäuren", "FM"),
    LANGKETTIGE_FETTSAEUREN("Langkettige Fettsäuren", "FL"),
    OMEGA_3_FETTSAEUREN("Omega-3-Fettsäuren", "FO3"), //TODO no usage
    OMEGA_6_FETTSAEUREN("Omega-6-Fettsäuren", "FO6"), //TODO no usage
    GLYCERIN_UND_LIPOIDE("Glycerin und Lipoide", "FG"),
    CHOLESTERIN("Cholesterin", "FC"),

    //Gesamtkennzahlen
    P_S_VERHAELTNIS("P/S Verhältnis", "GFPS", ""),
    BROTEINHEITEN("Broteinheiten", "GKB", "BE"),
    GESAMT_KOCHSALZ("Gesamt-Kochsalz", "GMKO"),
    MITTLERE_PORTIONSGROESSE("Mittlere Portionsgröße", "GP", "g/Port");

    private final static HashMap<String, BLSNutrient> LONG_NAME_TO_COLUMN_NAME_MAP = new HashMap<>();
    private final static HashMap<String, BLSNutrient> TABLE_COLUMN_NAME_TO_COLUMN_NAME_MAP = new HashMap<>();
    static {
        for (BLSNutrient columnName : values()) {
            LONG_NAME_TO_COLUMN_NAME_MAP.put(columnName.toString(), columnName);
            TABLE_COLUMN_NAME_TO_COLUMN_NAME_MAP.put(columnName.tableColumnName, columnName);
        }
    }

    private String longName;
    private String tableColumnName;
    private String unit = "mg/100 g";

    BLSNutrient(String longName, String tableColumnName) {
        this.longName = longName;
        this.tableColumnName = tableColumnName;
    }

    BLSNutrient(String longName, String tableColumnName, String unit) {
        this(longName, tableColumnName);
        this.unit = unit;
    }

    @Override
    public String toString() {
        return longName;
    }

    public String getLongName() {
        return longName;
    }

    public String getTableColumnName() {
        return tableColumnName;
    }

    public String getUnit() {
        return unit;
    }

    public static BLSNutrient getByString(String str) throws IllegalArgumentException {
        if(LONG_NAME_TO_COLUMN_NAME_MAP.containsKey(str)) {
            return LONG_NAME_TO_COLUMN_NAME_MAP.get(str);
        } else {
            throw new IllegalArgumentException("Cannot find enum constant for long name \"" + str + "\"");
        }
    }

    public static BLSNutrient getByTableColumnName(String tableColumnName) {
        if(TABLE_COLUMN_NAME_TO_COLUMN_NAME_MAP.containsKey(tableColumnName)) {
            return TABLE_COLUMN_NAME_TO_COLUMN_NAME_MAP.get(tableColumnName);
        } else {
            throw new IllegalArgumentException("Cannot find enum constant for table column name \"" + tableColumnName + "\"");
        }
    }
}
