package metapenta.tools.io.writers;

import metapenta.model.dto.ClusterReactionsDTO;
import metapenta.tools.io.utils.MetabolicNetworkJSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClusterReactionsWriter implements Writer {

    private static final String CLUSTER_REACTIONS = "clusterReactions";
    private ClusterReactionsDTO clusterReactionsDTO;

    private String fileName;

    public ClusterReactionsWriter(ClusterReactionsDTO clusterReactionsDTO, String fileName) {
        this.clusterReactionsDTO = clusterReactionsDTO;
        this.fileName = fileName;
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
        JSONObject reactionsObject = MetabolicNetworkJSONUtils.getMetabolicNetworkAsJSON(this.clusterReactionsDTO.getMetabolicNetwork());
        JSONObject clusterObject = clusterReactionObjects();
        reactionsObject.put(CLUSTER_REACTIONS, clusterObject);

        Files.write(Paths.get(this.fileName), reactionsObject.toJSONString().getBytes());
    }
}
