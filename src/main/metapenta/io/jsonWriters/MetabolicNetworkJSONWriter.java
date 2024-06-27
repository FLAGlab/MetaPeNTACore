package metapenta.io.jsonWriters;

import metapenta.model.MetabolicNetwork;
import metapenta.io.MetabolicNetworkJSONUtils;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MetabolicNetworkJSONWriter implements Writer {

    private MetabolicNetwork metabolicNetwork;
    private String fileName;

    public MetabolicNetworkJSONWriter(MetabolicNetwork metabolicNetwork, String fileName){
        this.fileName = fileName;
        this.metabolicNetwork = metabolicNetwork;
    }

    @Override
    public void write() throws IOException {
        JSONObject reactionsObject = MetabolicNetworkJSONUtils.getMetabolicNetworkAsJSON(this.metabolicNetwork);

        Files.write(Paths.get(this.fileName), reactionsObject.toJSONString().getBytes());
    }
}
