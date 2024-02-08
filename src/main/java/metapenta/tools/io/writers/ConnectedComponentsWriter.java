package metapenta.tools.io.writers;

import metapenta.model.dto.ConnectedComponentsDTO;
import metapenta.model.metabolic.network.Metabolite;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectedComponentsWriter implements Writer {
    private ConnectedComponentsDTO connectedComponents;

    private String filename;

    public ConnectedComponentsWriter(ConnectedComponentsDTO connectedComponents, String filename) {
        this.connectedComponents = connectedComponents;
        this.filename = filename;
    }

    public void write() throws IOException {
        JSONObject connectedComponentsJson = new JSONObject();
        Map<Integer, List<Metabolite>>  metabolitesConnected = connectedComponents.getConnectedMetabolites();
        Set<Integer> keySet = metabolitesConnected.keySet();
        for (Integer key: keySet) {
            JSONArray metabolitesJson = createMetaboliteJsonArray(metabolitesConnected.get(key));
            connectedComponentsJson.put(key, metabolitesJson);
        }

        Files.write(Paths.get(filename), connectedComponentsJson.toJSONString().getBytes());
    }

    private JSONArray createMetaboliteJsonArray(List<Metabolite> metabolites) {
        JSONArray metabolitesJson = new JSONArray();
        for (Metabolite m: metabolites) {
            metabolitesJson.add(m);
        }

        return metabolitesJson;
    }
}
