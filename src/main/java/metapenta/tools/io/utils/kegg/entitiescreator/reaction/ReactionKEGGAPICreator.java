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

    public final static String COBRA_DEFAULT_UPPER_BOUND = "cobra_default_up";
    public final static String COBRA_DEFAULT_LOWER_BOUND = "cobra_default_lb";
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
                COBRA_DEFAULT_LOWER_BOUND,
                COBRA_DEFAULT_UPPER_BOUND
        );

        return reaction;
    }

    public String ID() {
        return ID;
    }
}
