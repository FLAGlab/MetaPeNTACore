package metapenta.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import metapenta.dto.PathsDTO;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.petrinet.PetriNetElements;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;

public class AllPathsService {
    private int[] transitionVisited;
    private Place<Metabolite> currentSourcePlace;

    private ArrayList<Transition<Reaction>> currentPath = new ArrayList<>();

    private PathsDTO paths = new PathsDTO();

    private PetriNetElements metabolicPetriNet;

    private HashMap<String, Collection<Collection<Transition<Reaction>>>> memoizedRoutes = new HashMap<>();

    private Place target;
    private List<String> initPlaces;

    public AllPathsService(PetriNetElements metabolicPetriNet, FindAllPathsParams params) {
        this.metabolicPetriNet = metabolicPetriNet;
        this.initPlaces = params.getInitMetaboliteIds();
        this.target = metabolicPetriNet.getPlace(params.getTarget());
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

    private void visitTransitions(List<Transition> transitions) {
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

        List<Place> downPlaces = transition.getPlacesByCriteria(Transition.DOWN_CRITERIA);
        for (Place nextPlace: downPlaces) {
                visitPlace(nextPlace);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    private boolean transitionWasVisited(Transition<Reaction> transition){
        return transitionVisited[transition.getObject().getNid()] == 1;
    }

    private void markTransitionAsVisited(Transition<Reaction> transition){
        transitionVisited[transition.getObject().getNid()]++;
    }
}
