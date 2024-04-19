package metapenta.services;

import metapenta.commands.OrthogroupDTO;
import metapenta.model.dto.ClusterReactionsDTO;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.services.http.KEGGService;
import metapenta.tools.io.loaders.ClusterReactionsFileLoader;
import metapenta.tools.io.writers.ClusterReactionsWriter;

import java.io.*;
import java.util.*;

public class ClusterReactionsService {
    OrthogroupDTO otrhogroups;
    ClusterReactionsFileLoader clusterReactionsFileLoader = new ClusterReactionsFileLoader();
    KEGGService service = new KEGGService();
    String NGSEP_file;

    public ClusterReactionsService(String NGSEP_file) throws FileNotFoundException {
        this.NGSEP_file = NGSEP_file;
        this.otrhogroups = clusterReactionsFileLoader.load(NGSEP_file);
    }
    private ClusterReactionsDTO getClusterReactions() throws Exception {
        Map<Integer, Set<String>> clusterReactions = calculateReactionsByCluster();
        MetabolicNetwork metabolicNetwork = service.getMetabolicNetwork();

        return new ClusterReactionsDTO(metabolicNetwork, clusterReactions);
    }
    private Map<Integer, Set<String>> calculateReactionsByCluster() throws Exception {
        Map<Integer, Set<String>> clusterReactions = new HashMap<>();

        for(Integer clusterID: otrhogroups.getClusterKEGGGenesIds().keySet()){
            List<String> geneIds = otrhogroups.getClusterKEGGGenesIds().get(clusterID);
            Set<String> currentClusterReactions = new HashSet<>();
            for (String geneId : geneIds) {
                System.out.println("geneId: " + geneId);
                Set<String> reactions = service.getReactions(geneId);
                currentClusterReactions.addAll(reactions);
            }

            clusterReactions.put(clusterID, currentClusterReactions);
        }

        return clusterReactions;
    }

    public static void main(String[] args) throws Exception {
        ClusterReactionsService service = new ClusterReactionsService("data/NGSEP_Cluster_notations.txt");
        ClusterReactionsDTO clusterReactionsDTO = service.getClusterReactions();

        ClusterReactionsWriter writer = new ClusterReactionsWriter(clusterReactionsDTO, "out-examples/cluster-reactions/clusterReactionsExample_complete");
        writer.write();
    }
}
