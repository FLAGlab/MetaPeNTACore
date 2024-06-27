package metapenta.kegg;

import metapenta.model.Reaction;

import java.util.List;
import java.util.Map;

public class ReactionKEGGAPICreator implements EntityCreator<Reaction> {
    KEGGAPIHttp keggAPIHttp = new KEGGAPIHttp();

    public final static String COBRA_DEFAULT_UPPER_BOUND = "cobra_default_up";
    public final static String COBRA_DEFAULT_LOWER_BOUND = "cobra_default_lb";
    private final KEGGResponseParser parser = new KEGGResponseParser();

    public Reaction create(String id) {
        String reactionResponse = KEGGAPIResponse(id);
        if (reactionResponse.isEmpty()) {
        	//TODO: Check these reactions
            return new Reaction(id);
        }
        Map<String, List<String>> attributesMap = parser.parseGET(reactionResponse);
        Reaction reaction = createReactionFromMap(id, attributesMap);
        return reaction;
    }

    private String KEGGAPIResponse(String id){
        String reactionLink = KEGGUrlUtils.getEntry(id);
        String reactionResponse = keggAPIHttp.get(reactionLink);
        return reactionResponse;
    }

    private Reaction createReactionFromMap(String id, Map<String, List<String>> attributesMap) {
        ReactionAttributesParser parser = new ReactionAttributesParser(attributesMap);

        Reaction reaction = new Reaction(id,parser.reactionName(),parser.reactants(),parser.products());
        reaction.setEnzymes(parser.getEnzymes());
        reaction.setReversible(true);
        reaction.setLowerBoundFluxParameterId(COBRA_DEFAULT_LOWER_BOUND);
        reaction.setUpperBoundFluxParameterId(COBRA_DEFAULT_UPPER_BOUND);

        return reaction;
    }
    
}
