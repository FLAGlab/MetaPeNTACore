package metapenta.services;

import metapenta.model.dto.NetworkBoundaryDTO;
import metapenta.model.metabolic.network.Reaction;

import java.util.List;

public class NetworkBoundaryService {
    private final List<Reaction> exchangeReactions;


    public NetworkBoundaryService(List<Reaction> exchangeReactions){
        this.exchangeReactions = exchangeReactions;
    }

    public NetworkBoundaryDTO getNetworkBoundary() {
        return new NetworkBoundaryDTO(getExchangeReactions());
    }

    private List<Reaction> getExchangeReactions() {
        return exchangeReactions;
    }
}
