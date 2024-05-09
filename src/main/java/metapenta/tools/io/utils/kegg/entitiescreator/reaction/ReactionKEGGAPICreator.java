package metapenta.tools.io.utils.kegg.entitiescreator.reaction;

import metapenta.model.metabolic.network.Reaction;
import metapenta.tools.io.utils.kegg.KEGGResponseParser;
import metapenta.tools.io.utils.kegg.KEGGUrlUtils;
import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;
import metapenta.tools.io.utils.kegg.entitiescreator.KEGGAPIHttp;

import java.util.List;
import java.util.Map;

public class ReactionKEGGAPICreator implements EntityCreator {
    KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();
    private final KEGGResponseParser parser = new KEGGResponseParser();

    private String ID;
    public Reaction create(String ID) {
        this.ID = ID;

        String reactionResponse = KEGGAPIResponse(ID);
        if (reactionResponse.isEmpty()) {
            return new Reaction(ID);
        }

        Reaction reaction = createReaction(reactionResponse);
        return reaction;
    }

    private String KEGGAPIResponse(String id){
        String reactionLink = KEGGUrlUtils.getEntry(id);
        String reactionResponse = keggAPIHttp.get(reactionLink);

        return reactionResponse;
    }


    public Reaction createReaction(String body) {
        Map<String, List<String>> attributesMap = parser.parseGET(body);
        Reaction reaction = createReactionFromMap(attributesMap);

        return reaction;
    }

    private Reaction createReactionFromMap(Map<String, List<String>> attributesMap) {
        ReactionAttributesParser parser = new ReactionAttributesParser(attributesMap);

        Reaction reaction = new Reaction(
                parser.reactionID(),
                parser.reactionName(),
                parser.reactants(),
                parser.products(),
                parser.getEnzymes(),
                true,
                -10000.0,
                10000.0
        );

        return reaction;
    }

    public String ID() {
        return ID;
    }
}
