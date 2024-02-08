package metapenta.model.petrinet;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;

import java.util.*;

public class PetriNet {
    protected Map<String, Place<Metabolite>> places;
    protected Map<String, Transition<Reaction>> transitions;
    public PetriNet(){
        this.places = new TreeMap<>();
        this.transitions = new TreeMap<>();
    }
    public Map<String, Transition<Reaction>> getTransitions() {
        return transitions;
    }

    public Transition getTransition(String key){
        return transitions.get(key);
    }

    public Map<String, Place<Metabolite>> getPlaces() {
        return places;
    }

    public void AddTransition(String id, Transition<Reaction> transition){
        this.transitions.put(id, transition);
    }

    public void addPlace(String key, Place place){
        places.put(key, place);
    }

    public Place getPlace(String key){
        return places.get(key);
    }

    public List<String> getPlacesIds(){
        List<String> metabolitesIds = new ArrayList();
        Set<String> metabolitesKeys = places.keySet();

        for(String key: metabolitesKeys) {
            Metabolite metabolite = places.get(key).getObject();
            metabolitesIds.add(metabolite.getId());
        }

        return metabolitesIds;
    }

    public List<String> getTransitionsIds(){
        List<String> reactionIds = new ArrayList();
        Set<String> keys = transitions.keySet();

        for(String key: keys) {
            Reaction reaction = transitions.get(key).getObject();
            reactionIds.add(reaction.getId());
        }

        return reactionIds;
    }

    public List<Place<Metabolite>> getSources() {
        List<Place<Metabolite>> sourcePlaces = new ArrayList<>();
        Set<String> placesKeys = places.keySet();
        for(String placeKey: placesKeys) {
            if (places.get(placeKey).isSource()){
                sourcePlaces.add(places.get(placeKey));
            }
        }

        return sourcePlaces;
    }

    public List<Place<Metabolite>> getSinks() {
        List<Place<Metabolite>> sinkPlaces = new ArrayList<>();
        Set<String> placesKeys = places.keySet();
        for(String placeKey: placesKeys) {
            if (places.get(placeKey).isSource()){
                sinkPlaces.add(places.get(placeKey));
            }
        }

        return sinkPlaces;
    }
}
