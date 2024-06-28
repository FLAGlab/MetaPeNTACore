package metapenta.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import metapenta.io.jsonWriters.FindAllPathsWriter;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.MetabolicNetworkPetriNet;
import metapenta.model.Reaction;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;
import metapenta.services.dto.PathsDTO;

public class AllPathsService {
	
	
    private int[] transitionVisited;
    private Place<Metabolite> currentSourcePlace;

    private ArrayList<Transition<Reaction>> currentPath = new ArrayList<>();

    private PathsDTO paths = new PathsDTO();

    private MetabolicNetworkPetriNet metabolicPetriNet;

    private HashMap<String, Collection<Collection<Transition<Reaction>>>> memoizedRoutes = new HashMap<>();

    private Place<Metabolite> target;
    private List<String> initPlaces;
    
    public void setMetabolicNetwork(MetabolicNetwork metabolicNetwork) {
    	metabolicPetriNet = new MetabolicNetworkPetriNet(metabolicNetwork);	
	}
    public void setInitialMetaboliteIds(String initMetabolites) {
        String[] initialMetabolites = initMetabolites.split(",");

        for (int i = 0; i < initialMetabolites.length; i++) {
            initPlaces.add(initialMetabolites[i]);
        }
    }
    public void setInitialMetaboliteIds(List<String> metaboliteIds) {
		initPlaces = metaboliteIds;
	}
    public void setTargetId(String id) {
    	target = metabolicPetriNet.getPlace(id);
    }

    public PathsDTO getAllPaths() {
        for(String placeId: initPlaces) {
            Place place = metabolicPetriNet.getPlace(placeId);

            setCurrentSourcePlace(place);
            resetTransitionsVisited();
            findPathsFromPlace(place);
        }

        return paths;
    }
    private void resetTransitionsVisited() {
        transitionVisited = new int[metabolicPetriNet.getTransitions().size()];
    }

    private void setCurrentSourcePlace(Place<Metabolite> place){
        currentSourcePlace = place;
    }

    private void findPathsFromPlace(Place<Metabolite> source) {
        //checkSourceAndTargetPlace(source);
        visitPlace(source);
    }

    private void visitPlace(Place<Metabolite> place) {
        if (isCurrentPlaceTargetPlace(place)) {
            memoizeSubPaths();
            savePath(this.currentPath);
        }

        visitTransitions(place.getTransitionsByCriteria(Place.DOWN_CRITERIA));
    }

    private boolean isCurrentPlaceTargetPlace(Place<Metabolite> place) {
        return place.equals(target);
    }

    private void savePath(ArrayList<Transition<Reaction>> path) {
        paths.addPath(currentSourcePlace, (ArrayList<Transition<Reaction>>) path.clone());
    }

    private void calculateAndSaveAllRoutes(Transition transition) {
        Collection<Collection<Transition<Reaction>>> subPaths = memoizedRoutes.get(transition.getID());
        if (subPaths != null && !subPaths.isEmpty()) {
            for(Collection<Transition<Reaction>> sp: subPaths) {
                ArrayList<Transition<Reaction>> transitionPath = new ArrayList<>();
                transitionPath.addAll(currentPath);
                transitionPath.addAll(sp);
                savePath(transitionPath);
            }
        }
    }


    private void memoizeSubPaths(){
        for (int i = 0; i < currentPath.size(); i++){
            List<Transition<Reaction>> subPath =  currentPath.subList(i, currentPath.size());
            String originTransitionID = subPath.get(0).getID();

            Collection reactionList = memoizedRoutes.computeIfAbsent(originTransitionID, k -> new ArrayList<>());
            reactionList.add(new ArrayList<>(subPath));
        }
    }

    private void visitTransitions(List<Transition<?>> transitions) {
        for (Transition transition: transitions) {
            visitTransition(transition);
        }
    }

    private void visitTransition(Transition<Reaction> transition) {

        if(transitionWasVisited(transition)){
            calculateAndSaveAllRoutes(transition);
            return;
        }

        markTransitionAsVisited(transition);
        currentPath.add(transition);

        List<Place<?>> downPlaces = transition.getPlacesByCriteria(Transition.DOWN_CRITERIA);
        for (Place nextPlace: downPlaces) {
                visitPlace(nextPlace);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    private boolean transitionWasVisited(Transition<Reaction> transition){
        return transitionVisited[transition.getnId()] == 1;
    }

    private void markTransitionAsVisited(Transition<Reaction> transition){
        transitionVisited[transition.getnId()]++;
    }
    /**
     * args[0]: XML model
     * args[1]: Init metabolites separated by comma
     * args[2]: Target metabolite
     * args[3] Output file
     */
    public static void main(String[] args) throws Exception {
    	AllPathsService instance = new AllPathsService();
        instance.setMetabolicNetwork(MetabolicNetwork.load(args[0]));
        instance.setInitialMetaboliteIds(args[1]);
        instance.setTargetId(args[2]);
        PathsDTO paths = instance.getAllPaths();

        FindAllPathsWriter findAllPathsWriter = new FindAllPathsWriter(args[3], paths);
        findAllPathsWriter.write();
    }
	

	
}
