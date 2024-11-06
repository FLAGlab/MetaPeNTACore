package metapenta.io.jsonWriters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import metapenta.services.dto.ShortestPathsDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ShortestPathWriter implements Writer {

    
    private ShortestPathsDTO shortestPathsDTO;

    private String filename;


    public ShortestPathWriter(ShortestPathsDTO shortestPathsDTO, String filename) {
        this.shortestPathsDTO = shortestPathsDTO;
        this.filename = filename;
    }

    @Override
    public void write() throws IOException {
    	JSONObject pathsObject = new JSONObject();
    	JSONArray pathJsonArray = pathJsonArray(shortestPathsDTO.getPath());

        pathsObject.put("PATH", pathJsonArray);
        Files.write(Paths.get(this.filename), pathsObject.toJSONString().getBytes());
    }

    private JSONArray pathJsonArray(List<String> path) {
        JSONArray pathArray = new JSONArray();
        for(String reactionId: path) {
            pathArray.add(reactionId);
        }
        return pathArray;
    }
}
