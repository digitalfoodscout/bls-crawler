package de.tu_dortmund.pgdfs.blscrawler;

import java.util.HashMap;

/**
 * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
 */
public enum BLSColumnName {
    //Schlüsselwerte
    SBLS("BLS-Schlüssel", ""),
    ST("Text", ""),
    STE("Text englisch", ""),

    //Zusammensetzung
    GCAL("Energie (Kilokalorien)", "kcal/100 g"),
    GJ("Energie (Kilojoule)", "kJ/100 g"),
    GCALZB("Energie inkl. Energie aus Ballaststoffen (Kilokalorien)", "kcal/100 g"),
    GJZB("Energie inkl. Energie aus Ballaststoffen (Kilojoule)", "kJ/100 g"),
    ZW("Wasser", "mg/100 g"),
    ZE("Eiweiß (Protein)", "mg/100 g"),
    ZF("Fett", "mg/100 g"),
    ZK("Kohlenhydrate, resorbierbar", "mg/100 g"),
    ZB("Ballaststoffe", "mg/100 g"),
    ZM("Mineralstoffe (Rohasche)", "mg/100 g"),
    ZO("Organische Säuren", "mg/100 g"),
    ZA("Alkohol (Ethanol)", "mg/100 g"),

    //Vitamine
    VA("Vitamin A-Retinoläquivalent", "µg/100 g"),
    VAR("Vitamin A-Retinol", "µg/100 g"),
    VAC("Vitamin A-Beta-Carotin", "µg/100 g"),
    VD("Vitamin D-Calciferole", "µg/100 g"),
    VE("Vitamin E-Alpha-Tocopheroläquivalent", "µg/100 g"),
    VEAT("Vitamin E-Alpha-Tocopherol", "µg/100 g"),
    VK("Vitamin K-Phyllochinon", "µg/100 g"),
    VB1("Vitamin B1-Thiamin", "µg/100 g"),
    VB2("Vitamin B2-Riboflavin", "µg/100 g"),
    VB3("Vitamin B3-Niacin, Nicotinsäure", "µg/100 g"),
    VB3A("Vitamin B3-Niacinäquivalent", "µg/100 g"),
    VB5("Vitamin B5-Pantothensäure", "µg/100 g"),
    VB6("Vitamin B6-Pyridoxin", "µg/100 g"),
    VB7("Vitamin B7-Biotin (Vitamin H)", "µg/100 g"),
    VB9G("Vitamin B9-gesamte Folsäure", "µg/100 g"),
    VB12("Vitamin B12-Cobalamin", "µg/100 g"),
    VC("Vitamin C-Ascorbinsäure", "µg/100 g"),

    //Mineralstoffe
    MNA("Natrium", "mg/100 g"),
    MK("Kalium", "mg/100 g"),
    MCA("Calcium", "mg/100 g"),
    MMG("Magnesium", "mg/100 g"),
    MP("Phosphor", "mg/100 g"),
    MS("Schwefel", "mg/100 g"),
    MCL("Chlorid", "mg/100 g"),

    //Spurenelemente
    MFE("Eisen", "µg/100 g"),
    MZN("Zink", "µg/100 g"),
    MCU("Kupfer", "µg/100 g"),
    MMN("Mangan", "µg/100 g"),
    MF("Fluorid", "µg/100 g"),
    MJ("Iodid", "µg/100 g"),

    //Kohlenhydratzusammensetzung
    KAM("Mannit", "mg/100 g"),
    KAS("Sorbit", "mg/100 g"),
    KAX("Xylit", "mg/100 g"),
    KA("Summe Zuckeralkohole", "mg/100 g"),
    KMT("Glucose (Traubenzucker)", "mg/100 g"),
    KMF("Fructose (Fruchtzucker)", "mg/100 g"),
    KMG("Galactose (Schleimzucker)", "mg/100 g"),
    KM("Monosaccharide (1 M)", "mg/100 g"),
    KDS("Saccharose (Rübenzucker)", "mg/100 g"),
    KDM("Maltose (Malzzucker)", "mg/100 g"),
    KDL("Lactose (Milchzucker)", "mg/100 g"),
    KD("Disaccharide (2 M)", "mg/100 g"),
    KMD("Zucker (gesamt)", "mg/100 g"),
    KPOR("Oligosaccharide, resorbierbar (3 - 9 M)", "mg/100 g"),
    KPON("Oligosaccharide, nicht resorbierbar", " mg/100 g"),
    KPG("Glykogen (tierische Stärke)", "mg/100 g"),
    KPS("Stärke", "mg/100 g"),
    KP("Polysaccharide (> 9 M)", "mg/100 g"),

    //Ballaststoffzusammensetzung
    KBP("Poly-Pentosen", "mg/100 g"),
    KBH("Poly-Hexosen", "mg/100 g"),
    KBU("Poly-Uronsäure", "mg/100 g"),
    KBC("Cellulose", "mg/100 g"),
    KBL("Lignin", "mg/100 g"),
    KBW("Wasserlösliche Ballaststoffe", "mg/100 g"),
    KBN("Wasserunlösliche Ballaststoffe", "mg/100 g"),

    //Aminosäuren (Eiweißzusammensetzung)
    EILE("Isoleucin", "mg/100 g"),
    ELEU("Leucin", "mg/100 g"),
    ELYS("Lysin", "mg/100 g"),
    EMET("Methionin", "mg/100 g"),
    ECYS("Cystein", "mg/100 g"),
    EPHE("Phenylalanin", "mg/100 g"),
    ETYR("Tyrosin", "mg/100 g"),
    ETHR("Threonin", "mg/100 g"),
    ETRP("Tryptophan", "mg/100 g"),
    EVAL(" Valin", "mg/100 g"),
    EARG("Arginin", "mg/100 g"),
    EHIS("Histidin", "mg/100 g"),
    EEA("Essentielle Aminosäuren", "mg/100 g"),
    EALA("Alanin", "mg/100 g"),
    EASP("Asparaginsäure", "mg/100 g"),
    EGLU("Glutaminsäure", "mg/100 g"),
    EGLY("Glycin", "mg/100 g"),
    EPRO("Prolin", "mg/100 g"),
    ESER("Serin", "mg/100 g"),
    ENA("Nichtessentielle Aminosäuren", "mg/100 g"),
    EH("Harnsäure", "mg/100 g"),
    EP("Purin", "mg/100 g"),

    //Fettzusammensetzung (Fettsäuren)
    F40("Butansäure/Buttersäure", "mg/100 g"),
    F60("Hexansäure/Capronsäure", "mg/100 g"),
    F80("Octansäure/Caprylsäure", "mg/100 g"),
    F100("Decansäure/Caprinsäure", "mg/100 g"),
    F120("Dodecansäure/Laurinsäure", "mg/100 g"),
    F140("Tetradecansäure/Myristinsäure", "mg/100 g"),
    F150("Pentadecansäure", "mg/100 g"),
    F160("Hexadecansäure/Palmitinsäure", "mg/100 g"),
    F170("Heptadecansäure", "mg/100 g"),
    F180("Octadecansäure/Stearinsäure", "mg/100 g"),
    F200("Eicosansäure/Arachinsäure", "mg/100 g"),
    F220("Docosansäure/Behensäure", "mg/100 g"),
    F240("Tetracosansäure/Lignocerinsäure", "mg/100 g"),
    FS("Gesättigte Fettsäuren", "mg/100 g"),
    F141("Tetradecensäure", "mg/100 g"),
    F151("Pentadecensäure", "mg/100 g"),
    F161("Hexadecensäure/Palmitoleinsäure", "mg/100 g"),
    F171("Heptadecensäure", "mg/100 g"),
    F181("Octadecensäure/Ölsäure", "mg/100 g"),
    F201("Eicosensäure", "mg/100 g"),
    F221("Docosensäure/Erucasäure", "mg/100 g"),
    F241("Tetracosensäure/Nervonsäure", "mg/100 g"),
    FU("Einfach ungesättigte Fettsäuren", "mg/100 g"),
    F162("Hexadecadiensäure", "mg/100 g"),
    F164("Hexadecatetraensäure", "mg/100 g"),
    F182("Octadecadiensäure/Linolsäure", "mg/100 g"),
    F183("Octadecatriensäure/Linolensäure", "mg/100 g"),
    F184("Octadecatetraensäure/Stearidonsäure", "mg/100 g"),
    F193("Nonadecatriensäure", "mg/100 g"),
    F202("Eicosadiensäure", "mg/100 g"),
    F203("Eicosatriensäure", "mg/100 g"),
    F204("Eicosatetraensäure/Arachidonsäure", "mg/100 g"),
    F205("Eicosapentaensäure", "mg/100 g"),
    F222("Docosadiensäure", "mg/100 g"),
    F223("Docosatriensäure", "mg/100 g"),
    F224("Docosatetraensäure", "mg/100 g"),
    F225("Docosapentaensäure", "mg/100 g"),
    F226("Docosahexaensäure", "mg/100 g"),
    FP("Mehrfach ungesättigte Fettsäuren", "mg/100 g"),
    FK("Kurzkettige Fettsäuren", "mg/100 g"),
    FM("Mittelkettige Fettsäuren", "mg/100 g"),
    FL("Langkettige Fettsäuren", "mg/100 g"),
    FO3("Omega-3-Fettsäuren", "mg/100 g"),
    FO6("Omega-6-Fettsäuren", "mg/100 g"),
    FG("Glycerin und Lipoide", "mg/100 g"),
    FC("Cholesterin", "mg/100 g"),

    //Gesamtkennzahlen
    GFPS("P/S Verhältnis", ""),
    GKB("Broteinheiten", "BE"),
    GMKO("Gesamt-Kochsalz", "mg/100 g"),
    GP("Mittlere Portionsgröße", "g/Port");

    /**
     * @author <a href="mailto:dominik.krueger@tu-dortmund.de">Dominik Kr&uuml;ger</a>
     */
    private final static HashMap<String, BLSColumnName> VARIABLE_TO_COLUMN_NAME_MAP = new HashMap<String, BLSColumnName>();
    static {
        for (BLSColumnName columnName : values()) {
            VARIABLE_TO_COLUMN_NAME_MAP.put(columnName.toString(), columnName);
        }
    }

    private String variable;
    private String dimension;

    BLSColumnName(String variable, String dimension) {
        this.variable = variable;
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return this.variable;
    }

    public static BLSColumnName getByString(String variable) throws IllegalArgumentException {
        if(VARIABLE_TO_COLUMN_NAME_MAP.containsKey(variable)) {
            return VARIABLE_TO_COLUMN_NAME_MAP.get(variable);
        } else {
            throw new IllegalArgumentException("Cannot find enum constant for string \"" + variable + "\"");
        }
    }

}
