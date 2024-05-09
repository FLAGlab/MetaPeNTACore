package metapenta.tools.io.utils.kegg.entitiescreator.reaction;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.ReactionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactionAttributesParser {

    public static final String NAME = "NAME";
    private static final String REACTION_ENTRY = "ENTRY";

    private static final String REACTION_EQUATION = "EQUATION";
    private static final String ENZYME = "ENZYME";

    private Map<String, List<String>> attributesMap;

    private List<ReactionComponent> reactants = new ArrayList<>();

    private List<ReactionComponent> product = new ArrayList<>();

    public ReactionAttributesParser(Map<String, List<String>> attributesMap) {
        this.attributesMap = attributesMap;
        createProductsAndReactants();
    }

    private void createProductsAndReactants(){
        String equation = removeParentheses(attributesMap.get(REACTION_EQUATION).get(0));

        System.out.println("Reaction equation: " + equation);

        String[] equationComponents = equation.split("<=>");
        String reactants = equationComponents[0].trim();
        String products = equationComponents[1].trim();

        List<ReactionComponent> reactantsList = createBareBoneReactionComponent(reactants);
        List<ReactionComponent> productsList = createBareBoneReactionComponent(products);

        this.reactants = reactantsList;
        this.product = productsList;
    }

    public List<ReactionComponent> reactants() {
        return reactants;
    }
    public List<ReactionComponent> products() {
        return product;
    }

    public String reactionID() {
        String reactionID = getReactionIDFromEntry(attributesMap.get(REACTION_ENTRY));

        return reactionID;
    }
    private String getReactionIDFromEntry(List<String> entry) {
        String[] entryParts = entry.get(0).split("\s+");

        return entryParts[0];
    }

    public String reactionName() {
        String reactionName = "";
        if (attributesMap.get(NAME) != null) {
            reactionName = cleanMetaboliteName(attributesMap.get(NAME).get(0));
        }

        return reactionName;
    }

    private String cleanMetaboliteName(String name) {
        if (name != null && name.endsWith(",")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }


    public String removeParentheses(String input) {
        return input.replaceAll("\\(.*?\\)", "");
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
        double stoichiometry = metaboliteStoichiometry(productParts);
        if (stoichiometry == -1) {
            stoichiometry = 1.0;

            System.out.println("Error parsing stoichiometry for metabolite: " + metaboliteID + " in reaction: " + reactionComponentParams);
        }

        Metabolite metabolite = new Metabolite(metaboliteID);
        ReactionComponent reactionComponent = new ReactionComponent(metabolite, stoichiometry);

        return reactionComponent;
    }

    private String[] getProductParts(String product) {
        String[] productParts = product.split(" ");
        if (productParts.length == 1 ) {
            productParts = new String[]{"1", productParts[0]};
        }

        return productParts;
    }

    private double metaboliteStoichiometry(String[] productParts) {
        try {
            return Double.parseDouble(productParts[0]);
        } catch (NumberFormatException e){
            return -1;
        }
    }

    public List<GeneProduct> getEnzymes() {
        List<String> enzymes = attributesMap.getOrDefault(ENZYME, new ArrayList<>());

        return createBareBoneGeneProducts(enzymes);
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
}
