package metapenta.kegg;

import java.util.ArrayList;
import java.util.List;

public class GenEnzymesCreator implements EntityCreator<EntityList> {

    KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();
    private KEGGResponseParser parser = new KEGGResponseParser();

    @Override
    public EntityList create(String genID) {
        List<String> enzymeIDs = new ArrayList<>();
        String enzymeLink = KEGGUrlUtils.getEnzymeLink(genID);
        String response = keggAPIHttp.get(enzymeLink);
        if (!response.isEmpty()) {
            enzymeIDs = parser.parseLINKResponse(response);
        }
        return new EntityList(enzymeIDs, genID);
    }
}
