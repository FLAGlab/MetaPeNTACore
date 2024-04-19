package metapenta.tools.io.loaders;

import metapenta.commands.OrthogroupDTO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ClusterReactionsFileLoader {

    OrthogroupDTO clusterReactions = new OrthogroupDTO();
    public OrthogroupDTO load(String fileName) throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        List<String> lines =  bufferedReader.lines().toList();

        for (String line : lines) {
            String[] parts = line.split("\t");
            int clusterId = Integer.parseInt(parts[0]);
            String functionalAnnotation = parts[1];

            clusterReactions.addClusterFunctionalAnnotations(clusterId, List.of(functionalAnnotation.split(",")));
            addClusterKEGGGenesIds(clusterId, parts);
        }

        return clusterReactions;
    }

    private void addClusterKEGGGenesIds(int clusterId, String[] parts) {
        if (parts.length> 2) {
            String geneIds = parts[2];

            List<String> ids = List.of(geneIds.split(",")).stream().map(s -> s.substring(s.indexOf(":") + 1)).toList();
            clusterReactions.addClusterKEGGGenesIds(clusterId, ids);
        }
    }
}
