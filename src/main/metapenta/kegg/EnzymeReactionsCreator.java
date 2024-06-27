package metapenta.kegg;

import java.util.ArrayList;
import java.util.List;

public class EnzymeReactionsCreator implements EntityCreator<EntityList> {
    private KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();
    private KEGGResponseParser parser = new KEGGResponseParser();

    @Override
    public EntityList create(String enzymeID) {
        List<String> reactionsIDs = new ArrayList<>();
        String enzymeLink = KEGGUrlUtils.getReactionLink(enzymeID);
        String response = keggAPIHttp.get(enzymeLink);
        if (!response.isEmpty()) {
            reactionsIDs = parser.parseLINKResponse(response);
        }

        return new EntityList(reactionsIDs, enzymeID);
    }
}
