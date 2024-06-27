package metapenta.dto;

import metapenta.model.Reaction;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;

import java.util.ArrayList;
import java.util.List;

public class PathsDTO {
	//TODO: THis should not have petrinet objects
    List<List<Transition<Reaction>>> paths = new ArrayList<>();
    List<Place> initPlaces = new ArrayList<>();

    public void addPath(Place initPlace, List<Transition<Reaction>> reactions) {
        this.initPlaces.add(initPlace);
        this.paths.add(reactions);
    }

    public List<List<Transition<Reaction>>> getPaths() {
        return paths;
    }

    public List<Place> getInitPlaces() {
        return initPlaces;
    }
}
