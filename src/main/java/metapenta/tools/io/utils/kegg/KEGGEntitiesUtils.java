package metapenta.tools.io.utils.kegg;

import metapenta.model.dto.ProductAndReactantData;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KEGGEntitiesUtils {
    public static final String NAME = "NAME";
    private static final String REACTION_EQUATION = "EQUATION";
    private static final String REACTION_ENTRY = "ENTRY";

    public static final String COMPOUND_FORMULA = "FORMULA";

    private static final String ENZYME = "ENZYME";

    private String metaboliteID = "";
    private KEGGResponseParser parser = new KEGGResponseParser();

    private MetabolicNetworkElementsEnricher enricher = new MetabolicNetworkElementsEnricher();

    /**
     * This method create the based reaction object with the information from the KEGG response
     * This reaction object only contains the reaction ID, reaction name, reactants names and products names
     * @param body
     * @return Bare-bone reaction
     */
    public Reaction createBareBoneReaction(String body) {
        Map<String, List<String>> attributesMap = parser.parseGETResponse(body);
        Reaction reaction = createReactionFromMap(attributesMap);

        return reaction;
    }

    private Reaction createReactionFromMap(Map<String, List<String>> attributesMap) {
        String reactionID = getReactionIDFromMap(attributesMap);
        String reactionName = getReactionNameFromMap(attributesMap);
        ProductAndReactantData productAndReactantData = getProductsAndReactantsFormMap(attributesMap);
        List<GeneProduct> enzymes = getEnzymesFromMap(attributesMap);

        Reaction reaction = new Reaction(
            reactionID,
            reactionName,
            productAndReactantData.getReactantsList(),
            productAndReactantData.getProductsList(),
            enzymes,
            true,
            -10000.0,
            10000.0
        );

        return reaction;
    }

    private List<GeneProduct> getEnzymesFromMap(Map<String, List<String>> attributesMap) {
        List<String> enzymes = attributesMap.getOrDefault(ENZYME, new ArrayList<>());

        return createBareBoneGeneProducts(enzymes);
    }

    private ProductAndReactantData getProductsAndReactantsFormMap(Map<String, List<String>> attributesMap){
        String equation = removeParentheses(attributesMap.get(REACTION_EQUATION).get(0));

        String[] equationComponents = equation.split("<=>");
        String reactants = equationComponents[0].trim();
        String products = equationComponents[1].trim();

        List<ReactionComponent> reactantsList = createBareBoneReactionComponent(reactants);
        List<ReactionComponent> productsList = createBareBoneReactionComponent(products);

        ProductAndReactantData productAndReactantData = new ProductAndReactantData(reactantsList, productsList);

        return productAndReactantData;
    }

    private String getReactionIDFromMap(Map<String, List<String>> attributesMap) {
        String reactionID = getReactionIDFromEntry(attributesMap.get(REACTION_ENTRY));

        return reactionID;
    }
   private String getReactionNameFromMap(Map<String, List<String>> attributesMap ) {
       String reactionName = "";
       if (attributesMap.get(NAME) != null) {
           reactionName = cleanMetaboliteName(attributesMap.get(NAME).get(0));
       }

       return reactionName;
   }

    private List<GeneProduct> createBareBoneGeneProducts(List<String> enzymes) {
        List<GeneProduct> geneProducts = new ArrayList<>();
        for (String enzyme : enzymes) {
            List<GeneProduct> geneProductsCurrentEnzyme = createGeneProductFromEnzymeIDsLine(enzyme);
            geneProducts.addAll(geneProductsCurrentEnzyme);
        }

        return geneProducts;
    }
    private List<GeneProduct> createGeneProductFromEnzymeIDsLine(String enzymeIDsLine) {
        List<GeneProduct> geneProducts = new ArrayList<>();

        String[] enzymesIds = enzymeIDsLine.split("\s+");
        for(String enzymeId: enzymesIds){
            GeneProduct geneProduct = new GeneProduct(enzymeId, enzymeId);
            geneProducts.add(geneProduct);
        }

        return geneProducts;
    }

    private List<ReactionComponent> createBareBoneReactionComponent(String equation) {
        List<ReactionComponent> reactionComponents = new ArrayList<>();

        String[] products = equation.split("\\+");
        for (String product : products) {
            ReactionComponent reactionComponent = createMetaboliteFromID(product);
            reactionComponents.add(reactionComponent);
        }

        return reactionComponents;
    }

    private ReactionComponent createMetaboliteFromID(String reactionComponentParams) {
        String[] productParts = getProductParts(reactionComponentParams.trim());
        String metaboliteID = productParts[1];
        this.metaboliteID = metaboliteID;
        double stoichiometry = metaboliteStoichiometry(productParts);

        Metabolite metabolite = new Metabolite(metaboliteID);
        ReactionComponent reactionComponent = new ReactionComponent(metabolite, stoichiometry);

        return reactionComponent;
    }

    private double metaboliteStoichiometry(String[] productParts) {
        double stoichiometry = 1.0;

        try {
            stoichiometry = Double.parseDouble(productParts[0]);
        } catch (NumberFormatException e){
            System.out.println("Error parsing stoichiometry for metabolite: " + metaboliteID);
        }

        return stoichiometry;
    }

    private String[] getProductParts(String product) {
        String[] productParts = product.split(" ");
        if (productParts.length == 1 ) {
            productParts = new String[]{"1", productParts[0]};
        }

        return productParts;
    }

    private String getReactionIDFromEntry(List<String> entry) {
       String[] entryParts = entry.get(0).split("\s+");


       return entryParts[0];
    }

    public void enrichGeneProduct(GeneProduct geneProduct, String body) {
        enricher.enrichGeneProduct(geneProduct, body);
    }

    public void enrichReactionComponent(ReactionComponent r, String body){
        enricher.enrichReactionComponent(r, body);
    }

    private String cleanMetaboliteName(String name) {
        if (name != null && name.endsWith(",")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public List<String> getLinksIDs(String body) {
        return parser.parseLINKResponse(body);
    }

    public static String removeParentheses(String input) {
        return input.replaceAll("\\(.*?\\)", "");
    }
}
