package metapenta.tools.io.writers;

import metapenta.model.dto.ClusterReactionsDTO;
import metapenta.tools.io.utils.MetabolicNetworkJSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClusterReactionsWriter implements Writer {
    private ClusterReactionsDTO clusterReactionsDTO;

    private String clusterFile;

    private String metabolicNetworkFile;

    private  JSONObject clusterObject;

    private MetabolicNetworkXMLOutput metabolicNetworkXMLOutput = new MetabolicNetworkXMLOutput();

    public ClusterReactionsWriter(ClusterReactionsDTO clusterReactionsDTO, String prefix) {
        this.clusterReactionsDTO = clusterReactionsDTO;
        this.clusterFile = prefix + "_cluster_reactions.json" ;
        this.metabolicNetworkFile = prefix + "_metabolic_network.xml";

        prepareWriters();
    }

    private void prepareWriters() {
        clusterObject = clusterReactionObjects();
    }

    private JSONObject clusterReactionObjects() {
        JSONObject clusterReactionsObject = new JSONObject();
        for(Integer key: clusterReactionsDTO.getClusterReactions().keySet()) {
            JSONArray clusterReactions = MetabolicNetworkJSONUtils.getCollectionsJsonArray(clusterReactionsDTO.getClusterReactions().get(key));
            if(!clusterReactions.isEmpty()) {
                clusterReactionsObject.put(key, clusterReactions);
            }
        }
        return clusterReactionsObject;
    }

    public void write() throws IOException {
        metabolicNetworkXMLOutput.saveNetwork(clusterReactionsDTO.getMetabolicNetwork(), metabolicNetworkFile);
        Files.write(Paths.get(this.clusterFile), clusterObject.toJSONString().getBytes());
    }
}
