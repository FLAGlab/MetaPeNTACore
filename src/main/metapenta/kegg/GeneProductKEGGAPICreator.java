package metapenta.kegg;

import metapenta.model.GeneProduct;

import java.util.List;
import java.util.Map;

public class GeneProductKEGGAPICreator implements EntityCreator<GeneProduct> {
    private KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();

    private KEGGResponseParser parser = new KEGGResponseParser();
    
    @Override
    public GeneProduct create(String id) {
        if (!shouldEnrichGeneProduct(id)) {
            return new GeneProduct(id, id);
        }

        String response = keggAPIHttp.get(KEGGUrlUtils.getEntry(id));
        if (response.isEmpty()) {
            return new GeneProduct(id, id);
        }
        return new GeneProduct(id, getName(id,parser.parseGET(response)));
    }
    
    private String getName(String id, Map<String, List<String>> attributesMap) {
        String name = id;
        List<String> properties = attributesMap.get(KEGGEntities.NAME);
        if (properties != null && properties.size()>0) {
            name = properties.get(0);
        }
        return name;
    }


    private boolean shouldEnrichGeneProduct(String id) {
        // Enzyme families are not considered to be enriched
        if (id.contains("-")) {
            return false;
        }
        return true;
    }

}
