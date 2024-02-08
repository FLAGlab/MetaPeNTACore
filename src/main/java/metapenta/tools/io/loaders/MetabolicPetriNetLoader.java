package metapenta.tools.io.loaders;

import metapenta.model.*;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.petrinet.Edge;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetabolicPetriNetLoader {
    private MetabolicPetriNet petriNet = new MetabolicPetriNet();
    private MetabolicNetworkXMLLoader loader = new MetabolicNetworkXMLLoader();

    public MetabolicPetriNet load(String networkFile) throws Exception {
        try {
            MetabolicNetwork network = loader.loadNetwork(networkFile);
            loadPetriNet(network);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return petriNet;
    }

    private void loadPetriNet(MetabolicNetwork network){
        List<String> keysReaction = network.getReactionIds();
        for (String key : keysReaction) {
            loadReactionToPetriNet(network.getReaction(key));
        }
    }

    private void loadReactionToPetriNet(Reaction reaction) {
        Transition transition = this.createAndLoadTransitionToPetriNet(reaction);

        List<Edge> edgesIn = this.loadMetabolitesAndCreateEdgeList(reaction.getReactants());
        transition.AddEdgesIn(edgesIn);


        List<Edge> edgesOut = this.loadMetabolitesAndCreateEdgeList(reaction.getProducts());
        transition.AddEdgesOut(edgesOut);

        loadOutEdgesInPlacesOfTransition(transition);
        loadInEdgesInPlacesOfTransition(transition);
    }

    private Transition createAndLoadTransitionToPetriNet(Reaction reaction){
        Transition transition = petriNet.getTransition(reaction.getId());

        if ( transition == null ){
            transition = new Transition(reaction.getId(), reaction.getName(), reaction);
            petriNet.AddTransition(reaction.getId(), transition);
        }

        return transition;
    }

    private List<Edge> loadMetabolitesAndCreateEdgeList(List<ReactionComponent> reactionComponents){
        List<Edge> edges = new ArrayList<>();
        for (ReactionComponent reactionComponent : reactionComponents) {
            Metabolite metabolite = reactionComponent.getMetabolite();

            Place<Metabolite> place = petriNet.getPlace(metabolite.getId());
            if (place == null){
                place = createAndAddPlaceToNet(metabolite);
            }

            Edge<Place> edge = new Edge(place, reactionComponent.getStoichiometry());
            edges.add(edge);
        }

        return edges;
    }

    private Place createAndAddPlaceToNet(Metabolite metabolite){
        Place<Metabolite> place = new Place<>(metabolite.getId(), metabolite.getName(), metabolite);
        petriNet.addPlace(metabolite.getId(), place);

        return place;
    }

    private void loadOutEdgesInPlacesOfTransition(Transition transition) {
        List<Edge<Place>> edges = transition.getEdgesIn();
        for (Edge<Place> edge: edges) {
            Place place = edge.getTarget();

            Edge placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeOut(placeEdge);
        }
    }

    private void loadInEdgesInPlacesOfTransition(Transition transition) {
        List<Edge<Place>> edges = transition.getEdgesOut();
        for (Edge<Place> edge: edges) {
            Place place = edge.getTarget();

            Edge placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeIn(placeEdge);
        }
    }

}
