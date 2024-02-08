package metapenta.tools.io.utils;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import org.json.simple.JSONArray;

import java.util.Collection;

public class MetabolicNetworkJSONUtils {
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
}
