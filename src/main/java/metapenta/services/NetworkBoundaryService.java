package metapenta.services;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.dto.NetworkBoundaryDTO;
import metapenta.model.petrinet.Place;

import java.util.ArrayList;
import java.util.List;

public class NetworkBoundaryService {
    private final List<Place<Metabolite>> metabolites;

    public NetworkBoundaryService(List<Place<Metabolite>> boundaryMetabolites){
        this.metabolites = boundaryMetabolites;
    }

    public NetworkBoundaryDTO getNetworkBoundary() {
        return new NetworkBoundaryDTO(getMetabolitesSinks(), getMetabolitesSources());
    }

    private List<Metabolite> getMetabolitesSinks(){
        List<Metabolite> sinksMetabolites = new ArrayList<>();

        for(Place<Metabolite> place: metabolites) {
            if (place.isSink()) {
                sinksMetabolites.add(place.getObject());
            }
        }

        return sinksMetabolites;
    }

    private List<Metabolite> getMetabolitesSources(){
        List<Metabolite> sourcesMetabolites = new ArrayList<>();

        for(Place<Metabolite> place: metabolites) {
            if (place.isSource()) {
                sourcesMetabolites.add(place.getObject());
            }
        }

        return sourcesMetabolites;
    }
}
