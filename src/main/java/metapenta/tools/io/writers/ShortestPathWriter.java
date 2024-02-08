package metapenta.tools.io.writers;

import metapenta.model.dto.ShortestPathsDTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ShortestPathWriter implements Writer {

    private JSONObject pathsObject = new JSONObject();
    private ShortestPathsDTO shortestPathsDTO;

    private String filename;


    public ShortestPathWriter(ShortestPathsDTO shortestPathsDTO, String filename) {
        this.shortestPathsDTO = shortestPathsDTO;
        this.filename = filename;
    }

    @Override
    public void write() throws IOException {
        addPathsToJsonObject();
        Files.write(Paths.get(this.filename), pathsObject.toJSONString().getBytes());
    }


    private void addPathsToJsonObject(){
        for(String placeID: shortestPathsDTO.getPaths().keySet()){
            JSONArray pathsJsonArray = pathJsonArray(shortestPathsDTO.getPaths().get(placeID));

            pathsObject.put(placeID, pathsJsonArray);
        }
    }

    private JSONArray pathJsonArray(String[] path) {
        JSONArray pathArray = new JSONArray();

        for(String reactionId: path) {
            pathArray.add(reactionId);
        }

        return pathArray;
    }
}
