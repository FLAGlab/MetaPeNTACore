package metapenta.io;

import com.opencsv.CSVWriter;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetabolicNetworkCSVWriter {

    private MetabolicNetwork metabolicNetwork;

    private String fileName;

    private List<String[]> lines = new ArrayList<>();

    private static String REACTION_TO_METABOLITE = "reaction_metabolite";

    private static String METABOLITE_TO_REACTION = "metabolite_reaction";
    public MetabolicNetworkCSVWriter(MetabolicNetwork metabolicNetwork, String fileName){
        this.metabolicNetwork = metabolicNetwork;
        this.fileName = fileName;
    }

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
        try (CSVWriter csvWriter = new CSVWriter(fileWriter)) {
			csvWriter.writeAll(lines);
		}
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
            writeMetaboliteToReaction(reactionComponent.getMetaboliteId(), reaction.getId());
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
    /**
     * args[0]: Metabolic network in XML format
     * args[1]: Output file
     */
    public static void main(String[] args) throws Exception {
        MetabolicNetwork network = MetabolicNetwork.load(args[0]);

        MetabolicNetworkCSVWriter metabolicWriter = new MetabolicNetworkCSVWriter(network, args[1]);
        metabolicWriter.write();
    }

}
