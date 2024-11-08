package metapenta.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metapenta.io.jsonWriters.ConnectedComponentsWriter;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.MetabolicNetworkPetriNet;
import metapenta.model.Reaction;
import metapenta.model.petrinet.Edge;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;
import metapenta.services.dto.ConnectedComponentsDTO;

public class ConnectedComponentsService {
    private static final String DOWN_CRITERIA = "DOWN";

    private static final String UP_CRITERIA = "UP";
    private Map<String, Transition<Reaction>> transitions;

    private Map<Integer, List<Metabolite>> connectedComponentsPlaces = new HashMap<>();
    private Map<Integer, List<Reaction>> connectedComponentsTransitions = new HashMap<>();

    private int[] transitionsVisited;

    private int connectedComponentCurrentId = 0;


    public void setMetabolicNetwork(MetabolicNetwork network){
    	MetabolicNetworkPetriNet petriNet = new MetabolicNetworkPetriNet(network);
        this.transitions = petriNet.getTransitions();
        this.transitionsVisited = new int[transitions.size()];
    }

    public ConnectedComponentsDTO getConnectedComponents(){
        calculateConnectedComponents();
        return new ConnectedComponentsDTO(connectedComponentsPlaces, connectedComponentsTransitions);
    }


    private void calculateConnectedComponents() {
        for(String transitionId: transitions.keySet()) {
            Transition<Reaction> transition = transitions.get(transitionId);
            if(transitionsVisited[transition.getnId()] == 0) {
                visitTransition(transition);
                connectedComponentCurrentId++;
            }
        }
    }

    private void visitTransition(Transition transition) {
        markTransitionAsVisitedAndAssignGroupId(transition);
        assignGroupIdToTransitionPlaces(transition);
        visitTransitions(transition, DOWN_CRITERIA);
        visitTransitions(transition, UP_CRITERIA);
    }

    private void markTransitionAsVisitedAndAssignGroupId(Transition<Reaction> transition){
        transitionsVisited[transition.getnId()] = 1;

        List<Reaction> reactionList = connectedComponentsTransitions.computeIfAbsent(connectedComponentCurrentId, k -> new ArrayList<>());

        connectedComponentsTransitions.put(connectedComponentCurrentId, reactionList);
    }

    private void visitTransitions(Transition<Reaction> transition, String criteria){
        List<Transition<Reaction>> transitions = getTransitionsByCriteria(transition, criteria);
        for(Transition<Reaction> nextTransition: transitions) {
            if (transitionsHasNotBeenVisited(nextTransition)) {
                visitTransition(nextTransition);
            }
        }
    }

    private boolean transitionsHasNotBeenVisited(Transition<Reaction> transition) {
        return transitionsVisited[transition.getnId()] == 0;
    }

    private List<Transition<Reaction>> getTransitionsByCriteria(Transition<Reaction> transition, String criteria) {
        List<Transition<Reaction>> transitions = new ArrayList<>();
        List<Place<?>> places = transition.getPlacesByCriteria(criteria);

        for (Place place: places) {
            transitions.addAll(place.getTransitionsByCriteria(criteria));
        }

        return transitions;
    }

    private void assignGroupIdToPlace(Place<Metabolite> place){
        List<Metabolite> metaboliteList = connectedComponentsPlaces.computeIfAbsent(connectedComponentCurrentId, k -> new ArrayList<>());
        metaboliteList.add(place.getObject());
    }

    private void assignGroupIdToTransitionPlaces(Transition transition) {
        List<Edge<Place>> edgesIn = transition.getAllEdges();

        for (Edge<Place> edge: edgesIn) {
            Place place = edge.getTarget();
            assignGroupIdToPlace(place);
        }
    }
    /**
     * args[0]: Metabolic network in XML format
     * args[1]: Output file
     */
    public static void main(String[] args) throws Exception {
    	ConnectedComponentsService instance = new ConnectedComponentsService();
    	instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
		ConnectedComponentsWriter connectedComponentsWriter = new ConnectedComponentsWriter(instance.getConnectedComponents(), args[1]);
		connectedComponentsWriter.write();
	}
}
