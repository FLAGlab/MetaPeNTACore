package metapenta.model.dto;

import metapenta.model.MetabolicPetriNet;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;
import metapenta.services.ShortestPathByTransitionNumberService;

import java.util.HashMap;

public class ShortestPathsDTO {
    private Transition[] lastTransitions;

    private Place[] lastPlaces;

    private int[] distances;

    private MetabolicPetriNet petriNet;

    private HashMap<String, String[]> paths = new HashMap<>();

    public ShortestPathsDTO(int[] distances, Transition[] lastTransitions, Place[] lastPlaces, MetabolicPetriNet petriNet){
        this.distances = distances;
        this.lastTransitions = lastTransitions;
        this.lastPlaces = lastPlaces;
        this.petriNet = petriNet;

        calculatePaths();
    }

    private void calculatePaths() {
        for (int i = 0; i < distances.length; i++){
            if (distances[i] != ShortestPathByTransitionNumberService.INFINITE) {
                String[] path = calculatePath(i);
                paths.put(petriNet.getPlaceByNid(i).getID(), path);
            }
        }
    }

    private String[] calculatePath(int index) {
        String[] path = new String[distances[index]];

        for (int i = distances[index] - 1; i >=0 ; i--) {
            path[i] = lastTransitions[index].getID();

            Place<Metabolite> previousPlace = lastPlaces[index];

            index = previousPlace.getObject().getNid();
        }

        return path;
    }

    public HashMap<String, String[]> getPaths() {
        return paths;
    }
}
