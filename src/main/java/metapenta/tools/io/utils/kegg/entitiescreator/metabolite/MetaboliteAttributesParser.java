package metapenta.tools.io.utils.kegg.entitiescreator.metabolite;

import metapenta.tools.io.utils.kegg.KEGGEntities;

import java.util.List;
import java.util.Map;

public class MetaboliteAttributesParser {

    private Map<String, List<String>> attributesMap;
    public MetaboliteAttributesParser(Map<String, List<String>> attributesMap) {
        this.attributesMap = attributesMap;
    }


    public String ID() {
        // TODO: Implement this method
        return "";
    }
    public String name() {
        String name = "";
        List<String> properties = attributesMap.get(KEGGEntities.NAME);
        if (properties != null) {
            name = properties.get(0);
        }

        return name;
    }

    public String chemicalFormula(){
        String chemicalFormula = "";
        List<String> properties = attributesMap.get(KEGGEntities.COMPOUND_FORMULA);
        if (properties == null || properties.isEmpty() || properties.get(0).isEmpty()) {
            System.out.println("Compound formula not found for component ID: " +  attributesMap.get(KEGGEntities.NAME));
            return chemicalFormula;
        }

        return properties.get(0);
    }
}
