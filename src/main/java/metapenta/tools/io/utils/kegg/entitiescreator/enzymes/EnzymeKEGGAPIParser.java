package metapenta.tools.io.utils.kegg.entitiescreator.enzymes;

import java.util.List;
import java.util.Map;

public class EnzymeKEGGAPIParser {

    public static final String NAME = "NAME";
    private Map<String, List<String>> attributesMap;
    EnzymeKEGGAPIParser(Map<String, List<String>> attributesMap) {
        this.attributesMap = attributesMap;
    }

    public String id() {
        // TODO Implement me!
        return "";
    }

    public String name() {
        String name = "";
        List<String> properties = attributesMap.get(NAME);
        if (properties != null) {
            name = properties.get(0);
        }

        return name;
    }

}
