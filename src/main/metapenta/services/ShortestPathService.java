package metapenta.services;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.MetabolicNetworkPetriNet;
import metapenta.model.Reaction;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.PlaceComparable;
import metapenta.model.petrinet.Transition;
import metapenta.services.dto.ShortestPathsDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import metapenta.io.jsonWriters.ShortestPathWriter;


public class ShortestPathService {
    public static final int INFINITE = 100000;

    private MetabolicNetwork metabolicNetwork;

    public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
    	this.metabolicNetwork = metabolicNetwork;
	}

    public ShortestPathsDTO getShortestPath(String originId, String destinationId) {
    	List<String> path = this.calculateShortestPath(originId, destinationId);

        return new ShortestPathsDTO(metabolicNetwork, originId, destinationId, path);
    }

    private List<String> calculateShortestPath(String originId, String destinationId) {
    	MetabolicNetworkPetriNet metabolicPetriNet = new MetabolicNetworkPetriNet(metabolicNetwork);
    	int n = metabolicPetriNet.getPlaces().size();
    	int[]distances = new int[n];
    	Transition<Reaction>[] lastTransitions = new Transition[n];
        Place<Metabolite>[] lastPlaces = new Place[n];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = INFINITE;
        }
        Place<Metabolite> origin = metabolicPetriNet.getPlace(originId);
        
        if(origin == null) {
        	System.err.println("ERROR: Source metabolite "+originId+" not found");
        	return new ArrayList<>();
        }
        Place<Metabolite> dest = metabolicPetriNet.getPlace(destinationId);
        if(dest == null) {
        	System.err.println("ERROR: Destination metabolite "+destinationId+" not found");
        	return new ArrayList<>();
        }
        distances[origin.getnId()] = 0;
        PriorityQueue<PlaceComparable> queue = new PriorityQueue<>();
        Set<String> settled = new HashSet<>();
        queue.add(new PlaceComparable(origin, 0));

        while (!queue.isEmpty()) {
            PlaceComparable<Metabolite> current = queue.poll();
            settled.add(current.getID());
            List<Transition<?>> transitions = current.getTransitionsByCriteria(Place.DOWN_CRITERIA);

            for(Transition transition: transitions) {
                List<Place<Metabolite>> downEdges = transition.getPlacesByCriteria(Transition.DOWN_CRITERIA);

                for(Place<Metabolite> neighbourPlace : downEdges) {
                    if (!settled.contains(neighbourPlace.getID())){
                    	int newDistance = distances[current.getnId()] + 1;

                        if (newDistance < distances[neighbourPlace.getnId()]){
                            int nid = neighbourPlace.getnId();
                            distances[nid] = newDistance;
                            lastTransitions[nid] = transition;
                            lastPlaces[nid] = current;
                            queue.add(new PlaceComparable(neighbourPlace, newDistance));
                        }
                    }
                }
            }
        }
        return calculatePath(origin.getnId(), dest.getnId(), lastTransitions, lastPlaces);
    }
    private List<String> calculatePath(int originId, int destId, Transition<Reaction>[] lastTransitions, Place<Metabolite>[] lastPlaces) {
    	List<String> path = new ArrayList<>();
    	int nextId = destId;
    	if(lastTransitions[nextId]==null) return path;
        while(nextId!=originId) {
            path.add(lastTransitions[nextId].getID());
            Place<Metabolite> previousPlace = lastPlaces[nextId];
            nextId = previousPlace.getnId();
        }
        Collections.reverse(path);
        return path;
    }
    /**
     * The main method of class
     * args[0] the path of the XML file
     * args[1] initial metabolite to calculate shortest paths
     * args[2] Output file
     */
    public static void main(String[] args) throws Exception {
        ShortestPathService instance = new ShortestPathService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
        ShortestPathsDTO paths = instance.getShortestPath(args[1],args[2]);
        ShortestPathWriter shortestPathWriter = new ShortestPathWriter(paths, args[3]);
        shortestPathWriter.write();
    }
    
}
