package metapenta.services.http;

import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.tools.io.utils.kegg.KEGGEntitiesUtils;
import metapenta.tools.io.utils.kegg.KEGGUrlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class KEGGService {

    private KEGGUrlUtils utlUtils = new KEGGUrlUtils();
    private KEGGEntitiesUtils keggEntitiesUtils = new KEGGEntitiesUtils();
    private MetabolicNetwork metabolicNetwork = new MetabolicNetwork();

    public Set<String> getReactions(String genID) throws Exception {
        System.out.println("Processing gene: " + genID);
        List<String> enzymeIDs = getEnzymesIDs(genID);
        processEnzymesIDs(enzymeIDs);

        return processEnzymesIDs(enzymeIDs);
    }

    private Set<String> processEnzymesIDs(List<String> enzymeIDs ) throws Exception {
        Set<String> reactions = new TreeSet<>();
        for (String enzymeID : enzymeIDs) {
            System.out.println("Processing enzyme: " + enzymeID);
            List<String> reactionIDs = getReactionIDs(enzymeID);
            List<String> enzymeReaction = processReactionsIDs(reactionIDs);

            reactions.addAll(enzymeReaction);
        }

        return reactions;
    }

    private List<String> processReactionsIDs(List<String> reactionIDs) throws Exception {
        List<String> reactions = new ArrayList<>();
        for (String reactionID : reactionIDs) {
            if (metabolicNetwork.existsReaction(reactionID)){
                continue;
            }

            Reaction reaction = createReaction(reactionID);
            if (reaction == null) {
                continue;
            }
            metabolicNetwork.addReaction(reaction);
            reactions.add(reactionID);
        }

        return reactions;
    }

    private Reaction createReaction(String reactionID) throws Exception {
        String reactionLink = utlUtils.getEntryLink(reactionID);
        HttpResponse<String> reactionResponse = sendGetRequest(reactionLink);
        if (reactionResponse == null) {
            return null;
        }

        Reaction reaction = keggEntitiesUtils.createBareBoneReaction(reactionResponse.body());
        enrichReaction(reaction);

        return reaction;
    }

    private void enrichReaction(Reaction reaction) throws Exception {
        List<ReactionComponent> reactants = reaction.getReactants();
        enrichReactionComponents(reactants);

        List<ReactionComponent> products = reaction.getProducts();
        enrichReactionComponents(products);

        metabolicNetwork.addReaction(reaction);
    }

    private void enrichReactionComponents(List<ReactionComponent> compounds ) throws Exception {
        for (ReactionComponent reactant : compounds) {
            String metaboliteID = reactant.getMetabolite().getId();
            if (metabolicNetwork.existsMetabolite(metaboliteID)) {
                enrichFromMetabolicNetwork(reactant);
            } else {
                enrichFromKEGGAPI(reactant);
            }
        }
    }
    private void enrichFromKEGGAPI(ReactionComponent reactant) throws Exception {
        String compoundLink = utlUtils.getCompoundEntry(reactant.getMetabolite().getId());
        HttpResponse<String> compoundResponse = sendGetRequest(compoundLink);
        if (compoundResponse == null) {
            return;
        }

        keggEntitiesUtils.enrichReactionComponent(reactant, compoundResponse.body());
    }

    private void enrichFromMetabolicNetwork(ReactionComponent reactionComponent) {
        Metabolite metabolite = metabolicNetwork.getMetabolite(reactionComponent.getMetabolite().getId());
        if (metabolite != null) {
            reactionComponent.setMetabolite(metabolite);
        }
    }

    private List<String> getReactionIDs(String enzymeID) throws Exception {
        String reactionLink = utlUtils.getReactionLink(enzymeID);
        HttpResponse<String> reactionsResponse = sendGetRequest(reactionLink);
        if (reactionsResponse == null) {
            return new ArrayList<>();
        }

        return keggEntitiesUtils.getLinksIDs(reactionsResponse.body());
    }

    private List<String> getEnzymesIDs(String genID) throws Exception {
        String enzymeLink = utlUtils.getEnzymeLink(genID);
        HttpResponse<String> enzymeResponse = sendGetRequest(enzymeLink);
        if (enzymeResponse == null) {
            return new ArrayList<>();
        }

        return keggEntitiesUtils.getLinksIDs(enzymeResponse.body());
    }

    private HttpResponse<String> sendGetRequest(String link) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(link))
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if ( response.statusCode() != 200 ) {
            System.out.println("There was an error processing the request: " + response.statusCode() + " " + response.body());
            return null;
        }


        return response;
    }

    public MetabolicNetwork getMetabolicNetwork(){
        return metabolicNetwork;
    }
}
