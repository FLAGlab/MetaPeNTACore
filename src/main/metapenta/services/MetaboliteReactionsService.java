package metapenta.services;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.dto.MetaboliteReactionsDTO;
import metapenta.petrinet.Edge;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;

public class MetaboliteReactionsService {

    private static final String IS_PRODUCT = "is_product";

    private static final String IS_SUBSTRATE = "is_substrate";

    private MetabolicNetwork metabolicNetwork;

    private Metabolite metabolite;

    public MetaboliteReactionsService(MetabolicNetwork metabolicNetwork, Metabolite metabolite) {
        this.metabolicNetwork = metabolicNetwork;
        this.metabolite = metabolite;
    }

    public MetaboliteReactionsDTO getMetaboliteReactions() {
        List<Reaction> isSubstrate = getReactionsByCriteria(IS_SUBSTRATE);
        List<Reaction> isProduct = getReactionsByCriteria(IS_PRODUCT);
        return new MetaboliteReactionsDTO(isSubstrate, isProduct);
    }

    private List<Reaction> getReactionsByCriteria(String criteria){
        List<Reaction> reactions = metabolicNetwork.getReactionsAsList();
        
        for(Reaction r:reactions) {
        	List<ReactionComponent> metabolites = new ArrayList<ReactionComponent>();
        	if(IS_SUBSTRATE==criteria) {
        		metabolites = r.getReactants();
        	}
        	if(IS_PRODUCT==criteria) {
        		metabolites = r.getProducts();
        	}
        	for(ReactionComponent c:metabolites) {
        		if(c.getMetaboliteId().equals(metabolite.getId())) {
        			reactions.add(r);
        			break;
        		}
        	}
        }
        return reactions;
    }

    private List<Edge<Transition<Reaction>>> getEgeByCriteria(Place place , String criteria) {
        switch (criteria) {
            case (IS_PRODUCT):
                return place.getEdgesIn();
            case (IS_SUBSTRATE):
                return place.getEdgesOut();
        }

        return new ArrayList<>();
    }
}
