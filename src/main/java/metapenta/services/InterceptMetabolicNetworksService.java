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
        	Reaction targetReaction = targetMetabolicNetwork.getReaction(reaction.ID());
            if( targetReaction != null ) {
                commonReactions.add(reaction);
                compareCommonReactions(reaction,targetReaction);
            }
        }
        List<Reaction> reactionsT = targetMetabolicNetwork.getReactionsAsList();
        for (Reaction reaction : reactionsT) {
        	if(originMetabolicNetwork.getReaction(reaction.ID())==null) {
        		System.out.println("Target reaction: "+reaction.ID()+" not found in origin");
        	}
        }
        return reactions;
    }

	private void compareCommonReactions(Reaction reaction1, Reaction reaction2) {
		String id = reaction1.ID();
		if(!reaction1.getName().equals(reaction2.getName())) {
			System.out.println("Reaction "+id+" Names differ. N1: "+reaction1.getName()+" N2: "+reaction2.getName());
		}
		if(reaction1.getLowerBoundFlux()!=reaction2.getLowerBoundFlux()) {
			System.out.println("Reaction "+id+" Lower flux bound differ. N1: "+reaction1.getLowerBoundFlux()+" N2: "+reaction2.getLowerBoundFlux());
		}
		if(reaction1.getUpperBoundFlux()!=reaction2.getUpperBoundFlux()) {
			System.out.println("Reaction "+id+" Upper flux bound differ. N1: "+reaction1.getUpperBoundFlux()+" N2: "+reaction2.getUpperBoundFlux());
		}
		if(reaction1.isReversible()!=reaction2.isReversible()) {
			System.out.println("Reaction "+id+" Reversibility differ. N1: "+reaction1.isReversible()+" N2: "+reaction2.isReversible());
		}
		if(reaction1.isBalanced()!=reaction2.isBalanced()) {
			System.out.println("Reaction "+id+" Balanced status differ. N1: "+reaction1.isBalanced()+" N2: "+reaction2.isBalanced());
		}
		
	}
}
