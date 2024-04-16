package metapenta.services;

import metapenta.model.dto.ClusterReactionsDTO;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.services.http.KEGGService;
import metapenta.tools.io.writers.ClusterReactionsWriter;

import java.io.*;
import java.util.*;

public class ClusterReactionsService {
    Map<Integer, List<String>> clusterFunctionalAnnotations = new HashMap<>();
    Map<Integer, List<String>> clusterKEGGGenesIds = new HashMap<>();

    private KEGGService service = new KEGGService();

    private String NGSEP_file;

    public ClusterReactionsService(String NGSEP_file) throws FileNotFoundException {
        this.NGSEP_file = NGSEP_file;
        readNGSEPFile();
    }

    private void readNGSEPFile() throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(NGSEP_file));

        List<String> lines =  bufferedReader.lines().toList();

        for (String line : lines) {
            String[] parts = line.split("\t");
            int clusterId = Integer.parseInt(parts[0]);
            String functionalAnnotation = parts[1];

            clusterFunctionalAnnotations.put(clusterId, List.of(functionalAnnotation.split(",")));
            addClusterKEGGGenesIds(clusterId, parts);
        }
    }

    private void addClusterKEGGGenesIds(int clusterId, String[] parts) {
        if (parts.length> 2) {
            String geneIds = parts[2];

            List<String> ids = List.of(geneIds.split(",")).stream().map(s -> s.substring(s.indexOf(":") + 1)).toList();
            clusterKEGGGenesIds.put(clusterId, ids);
        }
    }

    private Map<Integer, Set<String>> calculateReactionsByCluster() throws Exception {
        Map<Integer, Set<String>> clusterReactions = new HashMap<>();

        for(Integer clusterID: clusterKEGGGenesIds.keySet()){
            List<String> geneIds = clusterKEGGGenesIds.get(clusterID);
            Set<String> currentClusterReactions = new HashSet<>();
            for (String geneId : geneIds) {
                Set<String> reactions = service.getReactions(geneId);
                currentClusterReactions.addAll(reactions);
            }

            clusterReactions.put(clusterID, currentClusterReactions);
        }

        return clusterReactions;
    }

    private ClusterReactionsDTO getClusterReactions() throws Exception {
        Map<Integer, Set<String>> clusterReactions = calculateReactionsByCluster();
        MetabolicNetwork metabolicNetwork = service.getMetabolicNetwork();

        return new ClusterReactionsDTO(metabolicNetwork, clusterReactions);
    }

    public static void main(String[] args) throws Exception {
        ClusterReactionsService service = new ClusterReactionsService("data/NGSEP_Cluster_notations_reduced.txt");
        ClusterReactionsDTO clusterReactionsDTO = service.getClusterReactions();

        ClusterReactionsWriter writer = new ClusterReactionsWriter(clusterReactionsDTO, "out-examples/cluster-reactions/clusterReactionsExample_complete");
        writer.write();
    }
}
