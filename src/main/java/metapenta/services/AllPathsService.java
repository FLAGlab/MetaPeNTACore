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
import java.util.List;

public class AllPathsService {
    private int[] placesVisited;
    private Place<Metabolite> currentSourcePlace;

    private ArrayList<Transition> currentPath = new ArrayList<>();

    private PathsDTO paths = new PathsDTO();

    private MetabolicPetriNet metabolicPetriNet;

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
            resetPlacesVisited();
            findPathsFromPlace(place);
        }

        return paths;
    }

    private void resetPlacesVisited() {
        placesVisited = new int[metabolicPetriNet.getPlaces().size()];
    }

    private void setCurrentSourcePlace(Place<Metabolite> place){
        currentSourcePlace = place;
    }

    private void findPathsFromPlace(Place<Metabolite> source) throws SourceAndTargetPlacesAreEqualException {
        checkSourceAndTargetPlace(source);
        visitPlace(source);
    }

    private void visitPlace(Place<Metabolite> place){
        markPlaceAsVisited(place);

        if (isCurrentPlaceTargetPlace(place)) {
            savePath();
            return;
        }

        visitTransitions(place.getTransitionsByCriteria(Place.DOWN_CRITERIA));
    }

    private boolean isCurrentPlaceTargetPlace(Place<Metabolite> place) {
        return place.equals(target);
    }

    private void savePath() {
        paths.addPath(currentSourcePlace, (ArrayList<Transition>) currentPath.clone());


    }

    private void visitTransitions(List<Transition> transitions) {
        for (Transition transition: transitions) {
            visitTransition(transition);
        }
    }

    private void visitTransition(Transition<Reaction> transition) {
        currentPath.add(transition);

        List<Place> downPlaces = transition.getPlacesByCriteria(Transition.DOWN_CRITERIA);
        for (Place nextPlace: downPlaces) {
            if (placeHasNotBeenVisited(nextPlace)){
                visitPlace(nextPlace);
            }
        }

        currentPath.remove(currentPath.size() - 1);
    }


    private boolean placeHasNotBeenVisited(Place<Metabolite> place){
        return placesVisited[place.getObject().getNid()] == 0;
    }
    private void markPlaceAsVisited(Place<Metabolite> place){
        placesVisited[place.getObject().getNid()] = 1;
    }

    private void checkSourceAndTargetPlace(Place<Metabolite> source) throws SourceAndTargetPlacesAreEqualException {
        if(source.equals(target)) {
            throw new SourceAndTargetPlacesAreEqualException();
        }
    }
}
