package metapenta.tools.io.utils;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.networks.MetabolicNetwork;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;

public class MetabolicNetworkJSONUtils {

    private static final String REACTIONS = "reactions";

    private static final String METABOLITES = "metabolites";

    public static JSONArray getReactionsJsonArray(Collection<Reaction> reactions) {
        JSONArray reactionsJsonArray = new JSONArray();

        for(Reaction reaction: reactions) {
            reactionsJsonArray.add(reaction);
        }

        return reactionsJsonArray;
    }

    public static JSONArray getMetabolitesJsonArray(Collection<Metabolite> metabolites) {
        JSONArray metabolitesJsonArray = new JSONArray();

        for(Metabolite reaction: metabolites) {
            metabolitesJsonArray.add(reaction);
        }

        return metabolitesJsonArray;
    }

    public static JSONObject getMetabolicNetworkAsJSON(MetabolicNetwork mn) {
        JSONObject reactionsObject = new JSONObject();

        JSONArray reactions = MetabolicNetworkJSONUtils.getReactionsJsonArray(mn.getReactionsAsList());
        reactionsObject.put(REACTIONS, reactions);

        JSONArray metabolites = MetabolicNetworkJSONUtils.getMetabolitesJsonArray(mn.getMetabolitesAsList());
        reactionsObject.put(METABOLITES, metabolites);

        return reactionsObject;
    }

    public static JSONArray getCollectionsJsonArray(Collection reactions) {
        JSONArray reactionsJsonArray = new JSONArray();

        for(Object reaction: reactions) {
            reactionsJsonArray.add(reaction);
        }

        return reactionsJsonArray;
    }
}
