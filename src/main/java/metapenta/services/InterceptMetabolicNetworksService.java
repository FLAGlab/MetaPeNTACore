package metapenta.services;

import metapenta.model.metabolic.network.Reaction;
import metapenta.model.networks.MetabolicNetwork;

import java.util.*;

public class InterceptMetabolicNetworksService {

    public MetabolicNetwork interception(MetabolicNetwork originMetabolicNetwork, MetabolicNetwork targetMetabolicNetwork){
        List<Reaction> reactions = interceptionReactions(originMetabolicNetwork, targetMetabolicNetwork);

        MetabolicNetwork newMetabolicNetwork = new MetabolicNetwork();
        newMetabolicNetwork.addReactions(reactions);

        return newMetabolicNetwork;
    }

    private List<Reaction> interceptionReactions(MetabolicNetwork originMetabolicNetwork, MetabolicNetwork targetMetabolicNetwork){
        List<Reaction> commonReactions = new ArrayList<>();
        List<Reaction> reactions = originMetabolicNetwork.getReactionsAsList();
        for (Reaction reaction : reactions) {
            if( targetMetabolicNetwork.getReaction(reaction.getId()) != null ) {
                commonReactions.add(reaction);
            }
        }
        return reactions;
    }
}
