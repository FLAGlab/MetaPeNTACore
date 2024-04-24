package metapenta.tools.io.utils.kegg.entitiescreator.enzymes;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.tools.io.utils.kegg.KEGGResponseParser;
import metapenta.tools.io.utils.kegg.KEGGUrlUtils;
import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.KEGGAPIHttp;

import java.util.List;
import java.util.Map;

public class EnzymeKEGGAPICreator implements EntityCreator<GeneProduct> {
    private KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();

    private KEGGResponseParser parser = new KEGGResponseParser();

    private String ID;
    @Override
    public GeneProduct create(String ID) {
        this.ID = ID;
        if (!shouldEnrichEnzyme(ID)) {
            return new GeneProduct(ID, ID);
        }

        String enzymeResponse = keggAPIHttp.get(KEGGUrlUtils.getEntry(ID));
        if (enzymeResponse.isEmpty()) {
            return new GeneProduct(ID, ID);
        }

        return createEnzymeFromMap(parser.parseGET(enzymeResponse));
    }

    @Override
    public String ID() {
        return ID;
    }

    private GeneProduct createEnzymeFromMap(Map<String, List<String>> attributesMap){
        EnzymeKEGGAPIParser parser = new EnzymeKEGGAPIParser(attributesMap);

        return new GeneProduct(parser.id(), parser.name());
    }

    private boolean shouldEnrichEnzyme(String id) {
        // Enzyme families are not considered to be enriched
        if (id.contains("-")) {
            return false;
        }
        return true;
    }

}
