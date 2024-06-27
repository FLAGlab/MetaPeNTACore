package metapenta.io.jsonWriters;

import metapenta.dto.ClusterReactionsDTO;
import metapenta.io.MetabolicNetworkJSONUtils;
import metapenta.io.MetabolicNetworkXMLWriter;

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

    private MetabolicNetworkXMLWriter metabolicNetworkXMLWriter = new MetabolicNetworkXMLWriter();

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
        metabolicNetworkXMLWriter.saveNetwork(clusterReactionsDTO.getMetabolicNetwork(), metabolicNetworkFile);
        Files.write(Paths.get(this.clusterFile), clusterObject.toJSONString().getBytes());
    }
}
