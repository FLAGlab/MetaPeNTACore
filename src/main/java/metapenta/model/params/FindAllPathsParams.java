package metapenta.model.params;

import java.util.ArrayList;
import java.util.List;

public class FindAllPathsParams {

    private String target;
    private List<String> initMetaboliteIds = new ArrayList<>();


    public FindAllPathsParams( String listInitMetabolites, String targetMetaboliteID){
        this.target = targetMetaboliteID;
        loadInitMetabolitesIds(listInitMetabolites);
    }

    private void loadInitMetabolitesIds(String initMetabolites) {
        String[] initialMetabolites = initMetabolites.split(",");

        for (int i = 0; i < initialMetabolites.length; i++) {
            initMetaboliteIds.add(initialMetabolites[i]);
        }
    }

    public String getTarget() {
        return target;
    }

    public List<String> getInitMetaboliteIds() {
        return initMetaboliteIds;
    }
}
