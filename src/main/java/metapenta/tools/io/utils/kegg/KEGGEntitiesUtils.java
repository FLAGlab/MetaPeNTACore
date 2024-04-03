package metapenta.tools.io.utils.kegg;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KEGGEntitiesUtils {
    private static final String NAME = "NAME";
    private static final String REACTION_EQUATION = "EQUATION";
    private static final String REACTION_ENTRY = "ENTRY";

    private static final String COMPOUND_FORMULA = "FORMULA";
    private KEGGResponseParser parser = new KEGGResponseParser();

    /**
     * This method create the based reaction object with the information from the KEGG response
     * This reaction object only contains the reaction ID, reaction name, reactants names and products names
     * @param body
     * @return Bare-bone reaction
     */
    public Reaction createBareBoneReaction(String body) {
        Map<String, List<String>> attributesMap = parser.parseGetResponse(body);

        String reactionName = "";
        if (attributesMap.get(NAME) != null) {
            reactionName = attributesMap.get(NAME).get(0);
        }

        String reactionID = getReactionIDFromEntry(attributesMap.get(REACTION_ENTRY));

        String equation = removeParentheses(attributesMap.get(REACTION_EQUATION).get(0));
        System.out.println("equation: "+ equation);

        String[] equationComponents = equation.split("<=>");
        String reactants = equationComponents[0].trim();
        String products = equationComponents[1].trim();

        List<ReactionComponent> reactantsList = createReactionComponent(reactants);
        List<ReactionComponent> productsList = createReactionComponent(products);

        return new Reaction(reactionID, reactionName, reactantsList, productsList);
    }

    private List<ReactionComponent> createReactionComponent(String equation) {
        List<ReactionComponent> reactionComponents = new ArrayList<>();

        String[] products = equation.split("\\+");
        for (String product : products) {
            String[] productParts = getProductParts(product.trim());

            String metaboliteID = productParts[1];

            double stoichiometry = 1.0;

            try {
                stoichiometry = Double.parseDouble(productParts[0]);
            } catch (NumberFormatException e){
                System.out.println("Error parsing stoichiometry for metabolite: " + metaboliteID);
            }


            Metabolite metabolite = new Metabolite(metaboliteID);
            ReactionComponent reactionComponent = new ReactionComponent(metabolite, stoichiometry);
            reactionComponents.add(reactionComponent);
        }

        return reactionComponents;
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

    public void enrichReactionComponent(ReactionComponent r, String body){
        Map<String, List<String>> attributesMap = parser.parseGetResponse(body);

        String name = r.getMetabolite().getId();
        List<String> properties = attributesMap.get(NAME);
        if (properties != null) {
            r.getMetabolite().setName(properties.get(0));
        }


        properties = attributesMap.get(COMPOUND_FORMULA);
        if (properties == null || properties.isEmpty() || properties.get(0).isEmpty()) {
            System.out.println("Compound formula not found for component ID: " +  attributesMap.get(NAME));
        } else {
            System.out.println("properties "+ properties.get(0));
            r.getMetabolite().setChemicalFormula(properties.get(0));
        }
    }

    public List<String> getLinksIDs(String body) {
        return parser.parseLinkResponse(body);
    }

    public static String removeParentheses(String input) {
        return input.replaceAll("\\(.*?\\)", "");
    }
}
