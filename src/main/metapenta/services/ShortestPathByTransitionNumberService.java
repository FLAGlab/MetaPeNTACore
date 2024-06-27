package metapenta.services;

import metapenta.model.Metabolite;
import metapenta.petrinet.PlaceComparable;
import metapenta.model.Reaction;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import metapenta.dto.ShortestPathsDTO;
import metapenta.petrinet.PetriNetElements;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;


public class ShortestPathByTransitionNumberService {
    public static final int INFINITE = 100000;

    private PetriNetElements metabolicPetriNet;

    private Place<Metabolite> origin;

    private PriorityQueue<PlaceComparable> queue = new PriorityQueue<>();

    private int[] distances;

    private Transition[] lastTransitions;
    private Place[] lastPlaces;


    private Set<String> settled = new HashSet<>();


    public ShortestPathByTransitionNumberService(PetriNetElements metabolicPetriNet, Place origin){
        this.metabolicPetriNet = metabolicPetriNet;
        this.origin = origin;
        lastTransitions = new Transition[metabolicPetriNet.getPlaces().size()];
        lastPlaces = new Place[metabolicPetriNet.getPlaces().size()];
    }


    public ShortestPathsDTO getShortestPath() {
        this.calculateShortestPath();

        return new ShortestPathsDTO(distances, lastTransitions, lastPlaces, metabolicPetriNet);
    }

    private void calculateShortestPath() {
        initElements();

        while (!queue.isEmpty()) {
            PlaceComparable<Metabolite> current = queue.poll();
            settled.add(current.getID());

            visitNeighboursPlaces(current);
        }

    }

    private void initElements() {
        initDistances();
        queue.add(new PlaceComparable(origin, 0));
    }

    private void initDistances() {
        distances = new int[metabolicPetriNet.getPlaces().size()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = INFINITE;
        }

        distances[origin.getObject().getNid()] = 0;
    }

    private void visitNeighboursPlaces(PlaceComparable<Metabolite> currentPlace) {
         List<Transition> transitions = currentPlace.getTransitionsByCriteria(Place.DOWN_CRITERIA);

         for(Transition transition: transitions) {
             List<Place<Metabolite>> downEdges = transition.getPlacesByCriteria(Transition.DOWN_CRITERIA);

             for(Place<Metabolite> neighbourPlace : downEdges) {
                 if (!settled.contains(neighbourPlace.getID())){
                     checkDistanceAndAddToQueue(currentPlace, neighbourPlace, transition);
                 }
             }
         }
    }

    private void checkDistanceAndAddToQueue(PlaceComparable<Metabolite> currentPlace, Place<Metabolite> neighbourPlace, Transition<Reaction> currentTransition) {
        int newDistance = distances[currentPlace.getObject().getNid()] + 1;

        if (newDistance < distances[neighbourPlace.getObject().getNid()]){
            updatePath(neighbourPlace.getObject().getNid(), newDistance, currentPlace, currentTransition);

            queue.add(new PlaceComparable(neighbourPlace, newDistance));
        }
    }

    private void updatePath(int index, int distance, Place place, Transition transition){
        distances[index] = distance;
        lastTransitions[index] = transition;
        lastPlaces[index] = place;
    }
}
