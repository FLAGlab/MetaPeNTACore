package metapenta.model;

import metapenta.model.petrinet.Edge;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetabolicNetworkPetriNet {
    private final Map<String, Place<Metabolite>> places = new TreeMap<>();
    private final Map<String, Transition<Reaction>> transitions = new TreeMap<>();

    public MetabolicNetworkPetriNet(MetabolicNetwork metabolicNetwork) {
    	int nId = 0;
    	List<Metabolite> metabolites = metabolicNetwork.getMetabolitesAsList();
    	for(Metabolite m:metabolites) {
    		addPlace(new Place<Metabolite>(nId, m.getId(),m.getName(),m));
    		nId++;
    	}
    	nId = 0;
    	List<Reaction> reactions = metabolicNetwork.getReactionsAsList();
    	for(Reaction r:reactions) {
    		Transition<Reaction> transition = new Transition<Reaction>(nId, r.getId(), r.getName(), r);
    		this.transitions.put(transition.getID(), transition);
            List<Edge<Place<?>>> edgesIn = createEdgeList(transition.getObject().getReactants());
            transition.addEdgesIn(edgesIn);
            List<Edge<Place<?>>> edgesOut = createEdgeList(transition.getObject().getProducts());
            transition.addEdgesOut(edgesOut);

            loadOutEdgesInReactantPlaces(transition);
            loadInEdgesInProductPlaces(transition);
    	}
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

    public void addPlace(Place<Metabolite> place){
        places.put(place.getID(), place);
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


    public void loadEdges(Transition<Reaction> transition ) {

        
    }

    private List<Edge<Place<?>>> createEdgeList(List<ReactionComponent> reactionComponents){
        List<Edge<Place<?>>> edges = new ArrayList<>();
        for (ReactionComponent reactionComponent : reactionComponents) {
            Metabolite metabolite = reactionComponent.getMetabolite();

            Place<Metabolite> place = getPlace(metabolite.getId());
            if (place == null){
                throw new RuntimeException("Place not found for metabolite: "+metabolite.getId());
            }

            Edge<Place<?>> edge = new Edge<>(place, reactionComponent.getStoichiometry());
            edges.add(edge);
        }
        return edges;
    }

    

    private void loadOutEdgesInReactantPlaces(Transition<Reaction> transition) {
        List<Edge<Place<?>>> edges = transition.getEdgesIn();
        for (Edge<Place<?>> edge: edges) {
            Place<?> place = edge.getTarget();

            Edge<Transition<?>> placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeOut(placeEdge);
        }
    }

    private void loadInEdgesInProductPlaces(Transition<Reaction> transition) {
        List<Edge<Place<?>>> edges = transition.getEdgesOut();
        for (Edge<Place<?>> edge: edges) {
            Place<?> place = edge.getTarget();

            Edge<Transition<?>> placeEdge = new Edge<>(transition, edge.getWeight());
            place.addEdgeIn(placeEdge);
        }
    }
}
