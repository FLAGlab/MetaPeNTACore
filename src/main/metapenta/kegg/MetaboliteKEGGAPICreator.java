package metapenta.kegg;

import metapenta.model.Compartment;
import metapenta.model.IncorrectFormulaException;
import metapenta.model.Metabolite;

import java.util.List;
import java.util.Map;

public class MetaboliteKEGGAPICreator implements EntityCreator<Metabolite> {

    public static final Compartment DEFAULT_COMPARTMENT_KEGG = new Compartment("c","c");

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
        	//TODO: check what happen with these metabolites
            return  new Metabolite(id, id);
        }
        Map<String, List<String>> attributesMap = parser.parseGET(compoundResponse);
        return createMetabolite(id, attributesMap);
    }

    private Metabolite createMetabolite(String id, Map<String, List<String>> attributesMap) {
        Metabolite metabolite = new Metabolite(id, getName(id,attributesMap));
        metabolite.setCompartmentId(DEFAULT_COMPARTMENT_KEGG.getId());
        String formula = getChemicalFormula(id,attributesMap);
		try {
			metabolite.setChemicalFormula(formula);
		} catch (IncorrectFormulaException e) {
			System.err.println("Formula "+formula+" could not be parsed for metabolite: "+id);
			e.printStackTrace();
		}
        return metabolite;
    }
    private String getName(String id, Map<String, List<String>> attributesMap) {
        String name = id;
        List<String> properties = attributesMap.get(KEGGEntities.NAME);
        if (properties != null && properties.size()>0) {
            name = properties.get(0);
        }
        return name;
    }

    private String getChemicalFormula(String id, Map<String, List<String>> attributesMap){
        String chemicalFormula = "";
        List<String> properties = attributesMap.get(KEGGEntities.COMPOUND_FORMULA);
        if (properties == null || properties.isEmpty() || properties.get(0).isEmpty()) {
            System.out.println("Compound formula not found for metabolite: " +  id);
            return chemicalFormula;
        }

        return properties.get(0);
    }
}
