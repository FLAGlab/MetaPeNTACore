package metapenta.io.jsonWriters;

import metapenta.model.Reaction;
import metapenta.model.petrinet.Transition;
import metapenta.services.dto.PathsDTO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FindAllPathsWriter implements Writer{
    private PathsDTO paths;

    private String outFile;

    private JSONObject pathsObject = new JSONObject();
    public FindAllPathsWriter(String outFile, PathsDTO paths){
        this.outFile = outFile;
        this.paths = paths;
    }
    @Override
    public void write() throws IOException {
        addPathsToJsonObject();
        Files.write(Paths.get(this.outFile), pathsObject.toJSONString().getBytes());
    }

    private void addPathsToJsonObject() {
        for (int i = 0; i < paths.getInitPlaces().size(); i++){
            writePlacePath(i);
        }
    }

    private void writePlacePath(int index) {
        List<Reaction> reactions = transitionsToReactionArray(paths.getPaths().get(index));
        JSONArray reactionsJsonArray =  MetabolicNetworkJSONUtils.getReactionsJsonArray(reactions);

        pathsObject.put(paths.getInitPlaces().get(index).getID(), reactionsJsonArray);
    }


    private List<Reaction> transitionsToReactionArray(List<Transition<Reaction>> path){
        List<Reaction> reactions = new ArrayList<>();

        for (Transition<Reaction> transition: path) {
            reactions.add(transition.getObject());
        }

        return reactions;
    }
}
