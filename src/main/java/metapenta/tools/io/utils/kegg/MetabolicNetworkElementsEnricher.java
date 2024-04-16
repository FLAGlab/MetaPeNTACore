package metapenta.tools.io.utils.kegg;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.ReactionComponent;

import java.util.List;
import java.util.Map;

public class MetabolicNetworkElementsEnricher {

    private KEGGResponseParser parser = new KEGGResponseParser();
    public void enrichReactionComponent(ReactionComponent r, String body){
        Map<String, List<String>> attributesMap = parser.parseGETResponse(body);

        String name = r.getMetabolite().getId();
        List<String> properties = attributesMap.get(KEGGEntitiesUtils.NAME);
        if (properties != null) {
            name = properties.get(0);
        }
        r.getMetabolite().setName(name);

        properties = attributesMap.get(KEGGEntitiesUtils.COMPOUND_FORMULA);
        if (properties == null || properties.isEmpty() || properties.get(0).isEmpty()) {
            System.out.println("Compound formula not found for component ID: " +  attributesMap.get(KEGGEntitiesUtils.NAME));
        } else {
            r.getMetabolite().setChemicalFormula(properties.get(0));
        }
    }

    public void enrichGeneProduct(GeneProduct geneProduct, String body) {
        Map<String, List<String>> attributesMap = parser.parseGETResponse(body);

        String name = geneProduct.getId();
        List<String> properties = attributesMap.get(KEGGEntitiesUtils.NAME);
        if (properties != null) {
            name = properties.get(0);
        }
        geneProduct.setName(name);
    }

}
