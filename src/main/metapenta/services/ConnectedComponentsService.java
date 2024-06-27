package metapenta.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import metapenta.dto.ConnectedComponentsDTO;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.petrinet.Edge;
import metapenta.petrinet.PetriNetElements;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;

public class ConnectedComponentsService {
    private static final String DOWN_CRITERIA = "DOWN";

    private static final String UP_CRITERIA = "UP";
    private Map<String, Transition<Reaction>> transitions;

    private Map<Integer, List<Metabolite>> connectedComponentsPlaces = new HashMap<>();
    private Map<Integer, List<Reaction>> connectedComponentsTransitions = new HashMap<>();

    private int[] transitionsVisited;

    private int connectedComponentCurrentId = 0;


    public ConnectedComponentsService(PetriNetElements petriNet){
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
            if(transitionsVisited[transition.getObject().getNid()] == 0) {
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
        transitionsVisited[transition.getObject().getNid()] = 1;

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
        return transitionsVisited[transition.getObject().getNid()] == 0;
    }

    private List<Transition<Reaction>> getTransitionsByCriteria(Transition<Reaction> transition, String criteria) {
        List<Transition<Reaction>> transitions = new ArrayList<>();
        List<Place> places = transition.getPlacesByCriteria(criteria);

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
}
