package metapenta.tools.io.utils.kegg.entitiescreator.listcreator;

import metapenta.tools.io.utils.kegg.KEGGResponseParser;
import metapenta.tools.io.utils.kegg.KEGGUrlUtils;
import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.KEGGAPIHttp;

import java.util.ArrayList;
import java.util.List;

public class EnzymeReactionsCreator implements EntityCreator<EntityList> {
    KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();
    String ID;
    private KEGGResponseParser parser = new KEGGResponseParser();

    @Override
    public EntityList create(String enzymeID) {
        this.ID = enzymeID;
        List<String> reactionsIDs = new ArrayList<>();
        String enzymeLink = KEGGUrlUtils.getReactionLink(enzymeID);
        String response = keggAPIHttp.get(enzymeLink);
        if (!response.isEmpty()) {
            reactionsIDs = parser.parseLINKResponse(response);
        }

        return new EntityList(reactionsIDs, enzymeID);
    }
}
