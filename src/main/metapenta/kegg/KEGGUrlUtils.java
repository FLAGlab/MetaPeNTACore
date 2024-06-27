package metapenta.kegg;

public class KEGGUrlUtils {

    private static final String KEGG_URL_FORMAT = "https://rest.kegg.jp/%s/%s/%s";
    private static final String LINK_OPERATION = "link";
    private static final String GET_OPERATION = "get";
    private static final String ENZYME_DB = "enzyme";
    private static final String RN_DB = "rn";

    public static String getEnzymeLink(String geneID) {
        String url = String.format(KEGG_URL_FORMAT, LINK_OPERATION, ENZYME_DB, geneID);

        return url;
    }

    public static String getReactionLink(String ecNumber) {
        String url = String.format(KEGG_URL_FORMAT, LINK_OPERATION, RN_DB, ecNumber);

        return url;
    }

    public static String getEntry(String entryKey) {
        String url = String.format(KEGG_URL_FORMAT, GET_OPERATION, entryKey, "");

        return url;
    }
}
