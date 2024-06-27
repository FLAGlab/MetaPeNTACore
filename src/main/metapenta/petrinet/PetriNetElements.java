package metapenta.petrinet;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PetriNetElements {
    private final Map<String, Place<Metabolite>> places = new TreeMap<>();
    private final Map<String, Transition<Reaction>> transitions = new TreeMap<>();

    public PetriNetElements(MetabolicNetwork metabolicNetwork) {
		// TODO Auto-generated constructor stub
	}
	public Map<String, Transition<Reaction>> getTransitions() {
        return transitions;
    }
    public Transition<Reaction> getTransition(String id) {
        return transitions.get(id);
    }
    public Map<String, Place<Metabolite>> getPlaces() {
        return places;
    }
    public void addTransition(String id, Transition<Reaction> transition){
        this.transitions.put(id, transition);
    }

    public void addPlace(String id, Place<Metabolite> place){
        places.put(id, place);
    }

    public Place<Metabolite> getPlace(String id){
        return places.get(id);
    }

    public List<String> getPlacesIDs() {
        return new ArrayList<>(places.keySet());
    }

    public List<String> getTransitionsIDs(){
        return new ArrayList<>(transitions.keySet());
    }


    public void loadReactionToPetriNetwork(Reaction reaction) {
        Transition transition = this.createAndLoadTransitionToPetriNet(reaction);

        List<Edge> edgesIn = this.loadMetabolitesAndCreateEdgeList(reaction.getReactants());
        transition.AddEdgesIn(edgesIn);


        List<Edge> edgesOut = this.loadMetabolitesAndCreateEdgeList(reaction.getProducts());
        transition.AddEdgesOut(edgesOut);

        loadOutEdgesInReactantPlaces(transition);
        loadInEdgesInProductPlaces(transition);
    }

    private List<Edge> loadMetabolitesAndCreateEdgeList(List<ReactionComponent> reactionComponents){
        List<Edge> edges = new ArrayList<>();
        for (ReactionComponent reactionComponent : reactionComponents) {
            Metabolite metabolite = reactionComponent.getMetabolite();

            Place<Metabolite> place = getPlace(metabolite.getId());
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
        addPlace(metabolite.getId(), place);

        return place;
    }
    private Transition createAndLoadTransitionToPetriNet(Reaction reaction){
        Transition transition = getTransition(reaction.getId());

        if ( transition == null ){
            transition = new Transition(reaction.getId(), reaction.getName(), reaction);
            addTransition(reaction.getId(), transition);
        }

        return transition;
    }

    private void loadOutEdgesInReactantPlaces(Transition transition) {
        List<Edge<Place>> edges = transition.getEdgesIn();
        for (Edge<Place> edge: edges) {
            Place place = edge.getTarget();

            Edge placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeOut(placeEdge);
        }
    }

    private void loadInEdgesInProductPlaces(Transition transition) {
        List<Edge<Place>> edges = transition.getEdgesOut();
        for (Edge<Place> edge: edges) {
            Place place = edge.getTarget();

            Edge placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeIn(placeEdge);
        }
    }
}