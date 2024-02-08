package metapenta.tools.io.writers;

import metapenta.model.MetabolicNetwork;
import metapenta.tools.io.utils.MetabolicNetworkJSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MetabolicNetworkJSONWriter implements Writer {

    private MetabolicNetwork metabolicNetwork;
    private String fileName;

    private final String REACTIONS = "reactions";

    private final String METABOLITES = "metabolites";

    public MetabolicNetworkJSONWriter(MetabolicNetwork metabolicNetwork, String fileName){
        this.fileName = fileName;
        this.metabolicNetwork = metabolicNetwork;
    }

    @Override
    public void write() throws IOException {
        JSONObject reactionsObject = new JSONObject();

        JSONArray reactions = MetabolicNetworkJSONUtils.getReactionsJsonArray(metabolicNetwork.getReactions().values());
        reactionsObject.put(REACTIONS, reactions);

        JSONArray metabolites = MetabolicNetworkJSONUtils.getMetabolitesJsonArray(metabolicNetwork.getMetabolites().values());
        reactionsObject.put(METABOLITES, metabolites);

        Files.write(Paths.get(this.fileName), reactionsObject.toJSONString().getBytes());
    }
}
