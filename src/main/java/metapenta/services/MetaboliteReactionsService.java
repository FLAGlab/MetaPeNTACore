package metapenta.services;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.dto.MetaboliteReactionsDTO;
import metapenta.model.petrinet.Edge;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;

public class MetaboliteReactionsService {

    private static final String IS_PRODUCT = "is_product";

    private static final String IS_SUBSTRATE = "is_substrate";

    private MetabolicNetwork petriNet;

    private Metabolite metabolite;

    public MetaboliteReactionsService(MetabolicNetwork petriNet, Metabolite metabolite) {
        this.petriNet = petriNet;
        this.metabolite = metabolite;
    }

    public MetaboliteReactionsDTO getMetaboliteReactions() {
        List<Reaction> isSubstrate = getReactionsByCriteria(IS_SUBSTRATE);
        List<Reaction> isProduct = getReactionsByCriteria(IS_PRODUCT);
        return new MetaboliteReactionsDTO(isSubstrate, isProduct);
    }

    private List<Reaction> getReactionsByCriteria(String criteria){
        List<Reaction> reactions = new ArrayList<>();
        Place placeOfMetabolite = petriNet.getPlaces().get(metabolite.getId());

        List<Edge<Transition<Reaction>>> edges = getEgeByCriteria(placeOfMetabolite, criteria);
        for(Edge<Transition<Reaction>> edge: edges) {
            reactions.add(edge.getTarget().getObject());
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
