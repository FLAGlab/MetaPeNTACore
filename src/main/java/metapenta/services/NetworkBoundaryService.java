package metapenta.services;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.dto.NetworkBoundaryDTO;
import metapenta.model.petrinet.Place;

import java.util.ArrayList;
import java.util.List;

public class NetworkBoundaryService {
    private List<Place<Metabolite>> sinks;

    private List<Place<Metabolite>> sources;

    public NetworkBoundaryService(List<Place<Metabolite>> sinks, List<Place<Metabolite>> sources){
        this.sinks = sinks;
        this.sources = sources;
    }

    public NetworkBoundaryDTO getNetworkBoundary() {
        return new NetworkBoundaryDTO(getMetabolitesSinks(), getMetabolitesSources());
    }

    private List<Metabolite> getMetabolitesSinks(){
        List<Metabolite> sinksMetabolites = new ArrayList<>();

        for(Place<Metabolite> place: sinks) {
            sinksMetabolites.add(place.getObject());
        }

        return sinksMetabolites;
    }

    private List<Metabolite> getMetabolitesSources(){
        List<Metabolite> sourcesMetabolites = new ArrayList<>();

        for(Place<Metabolite> place: sources) {
            sourcesMetabolites.add(place.getObject());
        }

        return sourcesMetabolites;
    }
}
