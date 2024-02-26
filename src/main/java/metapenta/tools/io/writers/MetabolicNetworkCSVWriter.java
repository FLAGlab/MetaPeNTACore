package metapenta.tools.io.writers;

import com.opencsv.CSVWriter;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.networks.MetabolicNetworkElements;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MetabolicNetworkCSVWriter implements Writer {

    private MetabolicNetwork metabolicNetwork;

    private String fileName;

    private List<String[]> lines = new ArrayList<>();

    private static String REACTION_TO_METABOLITE = "reaction_metabolite";

    private static String METABOLITE_TO_REACTION = "metabolite_reaction";
    public MetabolicNetworkCSVWriter(MetabolicNetwork metabolicNetwork, String fileName){
        this.metabolicNetwork = metabolicNetwork;
        this.fileName = fileName;
    }


    @Override
    public void write() throws IOException {
        addHeader();
        prepareRelationLines();
        writeCSVFile();
    }

    private void addHeader() {
        String[] header = {"source","target","interaction"};
        lines.add(header);
    }

    private void writeCSVFile() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        csvWriter.writeAll(lines);
    }

    private void prepareRelationLines() {
        List<Reaction> reactions = metabolicNetwork.getReactionsAsList();
        for (Reaction reaction: reactions) {
            writeReactionRelations(reaction);
        }
    }

    private void writeReactionRelations(Reaction reaction) {
        writeReactionReactants(reaction);
        writeReactionProducts(reaction);
    }

    private void writeReactionReactants(Reaction reaction){
        List<ReactionComponent> reactants = reaction.getReactants();
        for (ReactionComponent reactionComponent: reactants) {
            writeMetaboliteToReaction(reactionComponent.getMetabolite().getId(), reaction.getId());
        }
    }

    private void writeMetaboliteToReaction( String metabolite, String reaction){
        String[] reactionToMetaboliteLine = {metabolite, reaction, METABOLITE_TO_REACTION};
        lines.add(reactionToMetaboliteLine);
    }

    private void writeReactionProducts(Reaction reaction) {
        List<ReactionComponent> products = reaction.getProducts();
        for (ReactionComponent reactionComponent: products) {
            writeReactionToMetabolite(reaction.getId(), reactionComponent.getMetabolite().getId());
        }
    }

    private void writeReactionToMetabolite(String reaction, String metabolite) {
        String[] reactionToMetaboliteLine = {reaction, metabolite, REACTION_TO_METABOLITE};
        lines.add(reactionToMetaboliteLine);
    }

}
