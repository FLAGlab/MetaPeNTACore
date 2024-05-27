package metapenta.tools.io.loaders;

import metapenta.commands.OrthogroupDTO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusterReactionsFileLoader {

    OrthogroupDTO clusterReactions = new OrthogroupDTO();
    public OrthogroupDTO load(String fileName) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
        	 String line = bufferedReader.readLine();

             while (line!=null) {
                 String[] parts = line.split("\t");
                 int clusterId = Integer.parseInt(parts[0]);
                 String functionalAnnotation = parts[1];

                 clusterReactions.addClusterFunctionalAnnotations(clusterId, List.of(functionalAnnotation.split(",")));
                 addClusterKEGGGenesIds(clusterId, parts);
                 line = bufferedReader.readLine();
             }
        }

        return clusterReactions;
    }

    private void addClusterKEGGGenesIds(int clusterId, String[] parts) {
        if (parts.length<= 2) return;
        String geneIdsStr = parts[2];

        //List<String> ids = List.of(geneIds.split(",")).stream().map(s -> s.substring(s.indexOf(":") + 1)).toList();
        List<String> ids = new ArrayList();
        String [] geneIds =  geneIdsStr.split(",");
        for(int i=0;i<geneIds.length;i++) {
        	String geneId = geneIds[i];
        	int p = geneId.indexOf(':');
        	if(p<0) {
        		//TODO: Handle situation
        		continue;
        	}
        	ids.add(geneId.substring(p+1));
        }
        
        clusterReactions.addClusterKEGGGenesIds(clusterId, ids);
    }
}
