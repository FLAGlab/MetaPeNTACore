package metapenta.model.dto;

import metapenta.model.metabolic.network.Reaction;

import java.util.List;

public class NetworkBoundaryDTO {
    private List<Reaction> exchangeReactions;

    public NetworkBoundaryDTO(List<Reaction> exchangeReactions){
        this.exchangeReactions = exchangeReactions;
    }

    public List<Reaction> getExchangeReactions() {
        return exchangeReactions;
    }
}
