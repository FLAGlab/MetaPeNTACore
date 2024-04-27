package metapenta.tools.io.writers;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.dto.NetworkBoundaryDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NetworkBoundaryWriter implements Writer {

    private static final String SINKS_JSON_KEY = "Sinks";

    private static final String SOURCES_JSON_KEY = "Sources";
    private NetworkBoundaryDTO networkBoundaryDTO;

    private String outPath;
    public NetworkBoundaryWriter(NetworkBoundaryDTO networkBoundaryDTO, String outPath){
        this.networkBoundaryDTO = networkBoundaryDTO;
        this.outPath = outPath;
    }

    private JSONObject getJsonBoundaryObject() {
        JSONObject networkBoundary = new JSONObject();
        networkBoundary.put(SINKS_JSON_KEY, getSinksJsonArray());
        networkBoundary.put(SOURCES_JSON_KEY, getSourcesJsonArray());

        return networkBoundary;
    }

    private JSONArray getSinksJsonArray() {
        JSONArray metabolites = new JSONArray();
        for(Metabolite metabolite: networkBoundaryDTO.getSinks()){
            metabolites.add(metabolite);
        }

        return metabolites;
    }

    private JSONArray getSourcesJsonArray() {
        JSONArray metabolites = new JSONArray();
        for(Metabolite metabolite: networkBoundaryDTO.getSources()){
            metabolites.add(metabolite);
        }

        return metabolites;
    }

    public void write() throws IOException {
        JSONObject jsonObject = getJsonBoundaryObject();

        Files.write(Paths.get(outPath), jsonObject.toJSONString().getBytes());
    }
}
