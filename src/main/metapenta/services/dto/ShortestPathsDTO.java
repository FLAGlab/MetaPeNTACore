package metapenta.services.dto;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.MetabolicNetworkPetriNet;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;
import metapenta.services.ShortestPathService;

import java.util.HashMap;

public class ShortestPathsDTO {
	//TODO: This should not be implementing logic and it should not have petrinet objects
    private Transition[] lastTransitions;

    private Place[] lastPlaces;

    private int[] distances;

    private MetabolicNetworkPetriNet petriNet;

    private HashMap<String, String[]> paths = new HashMap<>();

    public ShortestPathsDTO(int[] distances, Transition[] lastTransitions, Place[] lastPlaces, MetabolicNetworkPetriNet petriNet) {
        this.distances = distances;
        this.lastTransitions = lastTransitions;
        this.lastPlaces = lastPlaces;
        this.petriNet = petriNet;

        calculatePaths();
    }

    private void calculatePaths() {
        for (int i = 0; i < distances.length; i++){
            if (distances[i] != ShortestPathService.INFINITE) {
                String[] path = calculatePath(i);
                //paths.put(petriNet.getPlaceByNid(i).getID(), path);
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
