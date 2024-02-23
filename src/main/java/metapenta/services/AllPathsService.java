package metapenta.services;

import metapenta.model.MetabolicPetriNet;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.dto.PathsDTO;
import metapenta.model.errors.SourceAndTargetPlacesAreEqualException;
import metapenta.model.params.FindAllPathsParams;
import metapenta.model.petrinet.Place;
import metapenta.model.petrinet.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class AllPathsService {
    private int[] transitionVisited;
    private Place<Metabolite> currentSourcePlace;

    private ArrayList<Transition> currentPath = new ArrayList<>();


    private PathsDTO paths = new PathsDTO();

    private MetabolicPetriNet metabolicPetriNet;

    private HashMap<String, Collection<Collection<Transition>>> memoizedRoutes = new HashMap<>();

    private Place target;
    private List<String> initPlaces;

    public AllPathsService(MetabolicPetriNet metabolicPetriNet, FindAllPathsParams params) {
        this.metabolicPetriNet = metabolicPetriNet;
        this.initPlaces = params.getInitMetaboliteIds();
        this.target = metabolicPetriNet.getPlace(params.getTarget());
    }

    public PathsDTO getAllPaths() throws SourceAndTargetPlacesAreEqualException {
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

    private void findPathsFromPlace(Place<Metabolite> source) throws SourceAndTargetPlacesAreEqualException {
        checkSourceAndTargetPlace(source);
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

    private void savePath(ArrayList<Transition> path) {
        paths.addPath(currentSourcePlace, (ArrayList<Transition>) path.clone());
    }

    private void calculateAndSaveAllRoutes(Transition transition) {
        Collection<Collection<Transition>> subPaths = memoizedRoutes.get(transition.getID());
        if (subPaths != null && !subPaths.isEmpty()) {
            for(Collection<Transition> sp: subPaths) {
                ArrayList<Transition> transitionPath = new ArrayList<>();
                transitionPath.addAll(currentPath);
                transitionPath.addAll(sp);
                savePath(transitionPath);
            }
        }
    }


    private void memoizeSubPaths(){
        for (int i = 0; i < currentPath.size(); i++){
            System.out.println(memoizedRoutes.keySet().size());
            List<Transition> subPath =  currentPath.subList(i, currentPath.size());
            String originTransitionID = subPath.get(0).getID();

            Collection reactionList = memoizedRoutes.computeIfAbsent(originTransitionID, k -> new ArrayList<>());
            reactionList.add(subPath.stream().toList());
        }
    }

    private void visitTransitions(List<Transition> transitions) {
        for (Transition transition: transitions) {
            visitTransition(transition);
        }
    }

    private void visitTransition(Transition<Reaction> transition) {
        if(transition.getObject().getId().equals("R_GAPD")){
           System.out.println("GHJ");
        }
        if(transition.getObject().getId().equals("R_NADH16")){
           System.out.println("GHJ");
        }

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

    private void checkSourceAndTargetPlace(Place<Metabolite> source) throws SourceAndTargetPlacesAreEqualException {
        if(source.equals(target)) {
            throw new SourceAndTargetPlacesAreEqualException();
        }
    }
}
