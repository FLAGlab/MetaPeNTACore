package metapenta.model.dto;

import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.networks.MetabolicNetwork;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClusterReactionsDTO {

    MetabolicNetwork metabolicNetwork = new MetabolicNetwork();

    Map<Integer, Set<String>> clusterReactions = new HashMap<>();

    public ClusterReactionsDTO(MetabolicNetwork metabolicNetwork, Map<Integer, Set<String>>  clusterReactions) {
        this.metabolicNetwork = metabolicNetwork;
        this.clusterReactions = clusterReactions;
    }

    public MetabolicNetwork getMetabolicNetwork() {
        return metabolicNetwork;
    }

    public Map<Integer, Set<String>> getClusterReactions() {
        return clusterReactions;
    }


}
