package metapenta.tools.io.utils.kegg.entitiescreator.metabolite;

import metapenta.model.metabolic.network.ChemicalFormula;
import metapenta.model.metabolic.network.Metabolite;

import metapenta.tools.io.utils.kegg.KEGGResponseParser;
import metapenta.tools.io.utils.kegg.KEGGUrlUtils;
import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.KEGGAPIHttp;

import java.util.List;
import java.util.Map;

public class MetaboliteKEGGAPICreator implements EntityCreator<Metabolite> {

    private static final String DEFAULT_COMPARTMENT = "c";

    KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();

    private KEGGResponseParser parser = new KEGGResponseParser();

    @Override
    public Metabolite create(String id) {
        String compoundLink = KEGGUrlUtils.getEntry(id);
        String compoundResponse = keggAPIHttp.get(compoundLink);

        return createMetaboliteFromResponse(compoundResponse, id);
    }

    private Metabolite createMetaboliteFromResponse(String compoundResponse, String id) {
        if (compoundResponse.isEmpty()) {
            return  new Metabolite(id);
        }

        Map<String, List<String>> attributesMap = parser.parseGET(compoundResponse);
        Metabolite metabolite = createMetabolite(attributesMap);
        metabolite.setId(id);

        return metabolite;
    }

    private Metabolite createMetabolite(Map<String, List<String>> attributesMap) {
        MetaboliteAttributesParser metaboliteParser = new MetaboliteAttributesParser(attributesMap);
        ChemicalFormula chemicalFormula = new ChemicalFormula(metaboliteParser.chemicalFormula());

        Metabolite metabolite = new Metabolite(metaboliteParser.ID(), metaboliteParser.name(), chemicalFormula, DEFAULT_COMPARTMENT);

        return metabolite;
    }
}
