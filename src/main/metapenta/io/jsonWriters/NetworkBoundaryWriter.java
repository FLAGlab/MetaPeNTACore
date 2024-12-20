package metapenta.io.jsonWriters;

import metapenta.model.Reaction;
import metapenta.services.dto.NetworkBoundaryDTO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NetworkBoundaryWriter implements Writer {

    private static final String EXCHANGE_REACTION = "ExchangeReactions";
    private NetworkBoundaryDTO networkBoundaryDTO;
    private String outputFile;

    public NetworkBoundaryWriter(NetworkBoundaryDTO networkBoundaryDTO, String outputFile){
        this.networkBoundaryDTO = networkBoundaryDTO;
        this.outputFile = outputFile;
    }

    private JSONObject getJsonBoundaryObject() {
        JSONObject networkBoundary = new JSONObject();
        networkBoundary.put(EXCHANGE_REACTION, getExchangeReactions());

        return networkBoundary;
    }

    private JSONArray getExchangeReactions() {
        JSONArray exchangeReactions = new JSONArray();
        for(Reaction reaction: networkBoundaryDTO.getExchangeReactions()){
            exchangeReactions.add(reaction);
        }

        return exchangeReactions;
    }


    public void write() throws IOException {
        JSONObject jsonObject = getJsonBoundaryObject();

        Files.write(Paths.get(outputFile), jsonObject.toJSONString().getBytes());
    }
}
