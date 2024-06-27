package metapenta.services;

import metapenta.model.Reaction;

import java.util.ArrayList;
import java.util.List;

import metapenta.io.MetabolicNetworkXMLLoader;
import metapenta.io.jsonWriters.MetabolicNetworkJSONWriter;
import metapenta.model.MetabolicNetwork;


public class IntersectMetabolicNetworksService {

    public MetabolicNetwork intersect(MetabolicNetwork originMetabolicNetwork, MetabolicNetwork targetMetabolicNetwork){
        List<Reaction> reactions = intersectReactions(originMetabolicNetwork, targetMetabolicNetwork);

        MetabolicNetwork newMetabolicNetwork = new MetabolicNetwork();
        newMetabolicNetwork.addReactions(reactions);

        return newMetabolicNetwork;
    }

    private List<Reaction> intersectReactions(MetabolicNetwork originMetabolicNetwork, MetabolicNetwork targetMetabolicNetwork){
        List<Reaction> commonReactions = new ArrayList<>();
        List<Reaction> reactions = originMetabolicNetwork.getReactionsAsList();
        for (Reaction reaction : reactions) {
        	Reaction targetReaction = targetMetabolicNetwork.getReaction(reaction.getId());
            if( targetReaction != null ) {
                commonReactions.add(reaction);
                compareCommonReactions(reaction,targetReaction);
            }
        }
        List<Reaction> reactionsT = targetMetabolicNetwork.getReactionsAsList();
        for (Reaction reaction : reactionsT) {
        	if(originMetabolicNetwork.getReaction(reaction.getId())==null) {
        		System.out.println("Target reaction: "+reaction.getId()+" not found in origin");
        	}
        }
        return reactions;
    }

	private void compareCommonReactions(Reaction reaction1, Reaction reaction2) {
		String id = reaction1.getId();
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
	public static void main(String[] args) throws Exception {

        IntersectMetabolicNetworksService instance = new IntersectMetabolicNetworksService();
        MetabolicNetwork network1 = MetabolicNetwork.load(args[0]);
        MetabolicNetwork network2 = MetabolicNetwork.load(args[1]);

        MetabolicNetwork resultNetwork = instance.intersect(network1, network2);

        MetabolicNetworkJSONWriter metabolicNetworkWriter = new MetabolicNetworkJSONWriter(resultNetwork, args[2]);
        metabolicNetworkWriter.write();
    }
}
