package metapenta.tools.io.writers;

import metapenta.model.MetabolicPetriNet;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.petrinet.Transition;
import metapenta.tools.io.utils.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DescribeNetworkWriter implements Writer {

    private static final String COMPARTMENT_FILE_SUFFIX = "_metabolites.txt";
    private MetabolicPetriNet network;

    private StringBuilder metabolitesBuilder = new StringBuilder();

    private StringBuilder reactionsBuilder = new StringBuilder();

    private StringBuilder sMatrixBuilder = new StringBuilder();

    private StringBuilder reversibleRxnBuilder = new StringBuilder();

    private StringBuilder irreversibleRxnBuilder = new StringBuilder();

    private StringBuilder upBoundFileBuilder = new StringBuilder();

    private StringBuilder loBoundsFileBuilder = new StringBuilder();

    private StringUtils stringUtils =  new StringUtils();

    private Map<String, StringBuilder> compartmentsBuilders = new HashMap<>();

    private String metabolitesFileName;

    private String reactionsFile;

    private String sMatrixFile;

    private String reversibleRxnFile;

    private String irreversibleRxnFile;

    private String upBoundFile;

    private String loBoundsFile;

    private String filesPrefix;

    public DescribeNetworkWriter(MetabolicPetriNet network, String prefix){
        this.network = network;
        initFilePrefixes(prefix);
    }

    private void initFilePrefixes(String prefix) {
        this.metabolitesFileName = prefix + "_compounds.txt";
        this.reactionsFile = prefix + "_reactions.txt";
        this.sMatrixFile = prefix + "_s_matrix.txt";
        this.reversibleRxnFile = prefix + "_reversible_rxn.txt";
        this.upBoundFile = prefix + "_upperbounds_on_fluxes.txt";
        this.loBoundsFile = prefix + "_lowerbounds_on_fluxes.txt";
        this.irreversibleRxnFile = prefix + "_irreversible_reactions";
        this.filesPrefix = prefix;

    }

    @Override
    public void write() throws IOException {
        prepareFiles();
        writeFiles();
    }

    private void prepareFiles() {
        prepareCompartmentsFiles();
        prepareMetabolitesFile();
        prepareReactionsFile();
        prepareReversibleReactions();
        prepareIrreversibleReactions();
        writeSMatrix();
    }

    private void writeFiles() throws IOException {
        Files.write(Paths.get(metabolitesFileName), metabolitesBuilder.toString().getBytes());
        Files.write(Paths.get(reactionsFile), reactionsBuilder.toString().getBytes());
        Files.write(Paths.get(sMatrixFile), sMatrixBuilder.toString().getBytes());
        Files.write(Paths.get(reversibleRxnFile), reversibleRxnBuilder.toString().getBytes());
        Files.write(Paths.get(irreversibleRxnFile), irreversibleRxnBuilder.toString().getBytes());
        Files.write(Paths.get(upBoundFile), upBoundFileBuilder.toString().getBytes());
        Files.write(Paths.get(loBoundsFile), loBoundsFileBuilder.toString().getBytes());

        writeCompartmentFiles();
    }

    private void writeCompartmentFiles() throws IOException {
        Set<String> compartments = compartmentsBuilders.keySet();

        for(String compartment: compartments) {
            StringBuilder compartmentBuilder = compartmentsBuilders.get(compartment);
            Files.write(Paths.get(getCompartmentFileName(compartment)), compartmentBuilder.toString().getBytes());
        }
    }

    private String getCompartmentFileName(String compartment) {
        return filesPrefix + compartment + COMPARTMENT_FILE_SUFFIX;
    }


    private void writeSMatrix() {
        Set<String> keys = network.getTransitions().keySet();
        for (String key: keys){
            Transition<Reaction> transition = network.getTransition(key);
            Reaction reaction = transition.getObject();

            writeReactionComponentList(reaction.getReactants(), 1);
            writeReactionComponentList(reaction.getProducts(), -1);
        }

    }

    private void writeReactionComponentList(List<ReactionComponent> reactionComponents, double stoichiometryMultiplier) {
        for (ReactionComponent product: reactionComponents){
            Metabolite productMetabolite = product.getMetabolite();
            writeInSMatrix(productMetabolite.getId(), stoichiometryMultiplier * product.getStoichiometry());
        }
    }

    private void prepareMetabolitesFile(){
        List<String> metaboliteIds = network.getPlacesIds();
        for(String metaboliteId: metaboliteIds){
         writeMetabolite(metaboliteId);
        }
    }

    private void writeMetabolite(String metabolite){
        stringUtils
                .setString(metabolite)
                .addSingleQuotes()
                .addEmptySpace()
                .addBreakLine();

        metabolitesBuilder.append(stringUtils.GetString());
    }

    private void prepareReactionsFile(){
        List<String> reactionsIds = network.getTransitionsIds();
        for(String reactionId: reactionsIds){
            writeReaction(reactionId);
        }
    }

    private void prepareReversibleReactions(){
        List<String> reactionsIds = network.getReversibleReactionsIds();
        for(String reactionId: reactionsIds){
            WriteInReversibleReactions(reactionId);
            writeBoundsForReversibleReaction(reactionId);
        }
    }

    private void prepareIrreversibleReactions(){
        List<String> irreversibleReactionsIds = network.getIrreversibleReactionsIds();
        for(String reactionId: irreversibleReactionsIds){
            writeIrreversibleReaction(reactionId);
            writeBoundsForIrreversibleReaction(reactionId);
        }
    }


    private void writeReaction(String reactionID){
        stringUtils
                .setString(reactionID)
                .addSingleQuotes()
                .addBreakLine();

        reactionsBuilder.append(stringUtils.GetString());
    }

    private void writeInSMatrix(String metaboliteID, double stoichiometry){
        stringUtils.setString(metaboliteID)
                .addSingleQuotes()
                .addEmptySpace()
                .addDouble(stoichiometry)
                .addBreakLine();

        sMatrixBuilder.append(stringUtils.GetString());
    }

    private void WriteImUpBound(String s){
        upBoundFileBuilder.append(s);
    }

    private void WriteInReversibleReactions(String reactionID){
        stringUtils
                .setString(reactionID)
                .addSingleQuotes()
                .addBreakLine();

        reversibleRxnBuilder.append(stringUtils.GetString());
    }

    private void writeIrreversibleReaction(String reactionID){
        stringUtils
                .setString(reactionID)
                .addSingleQuotes()
                .addBreakLine();

        irreversibleRxnBuilder.append(stringUtils.GetString());
    }

    private void writeBoundsForReversibleReaction(String reactionName){
        stringUtils.
                setString(reactionName).
                addSingleQuotes().
                addEmptySpace().
                addInt(-1000).
                addBreakLine();

        WriteInLowBound(stringUtils.GetString());

        stringUtils.
                setString(reactionName);
        stringUtils.
                addSingleQuotes().
                addEmptySpace().
                addInt(1000).
                addBreakLine();

        WriteImUpBound(stringUtils.GetString());
    }

    private void writeBoundsForIrreversibleReaction(String reactionId){
        StringUtils stringUtils = new StringUtils();
        stringUtils.
                setString(reactionId).
                addSingleQuotes().
                addEmptySpace().
                addInt(0).
                addBreakLine();
        WriteInLowBound(stringUtils.GetString());

        stringUtils.
                setString(reactionId).
                addSingleQuotes().
                addEmptySpace().
                addInt(1000).
                addBreakLine();

        WriteImUpBound(stringUtils.GetString());
    }

    private void prepareCompartmentsFiles() {
        Map<String, List<Metabolite>> metabolitesByCompartments = network.getReactionsByCompartments();
        Set<String> compartments = metabolitesByCompartments.keySet();

        for(String compartment: compartments) {
            List<Metabolite> metabolites = metabolitesByCompartments.get(compartment);

            for(Metabolite metabolite: metabolites) {
                StringBuilder compartmentBuilder = getCompartmentBuilder(compartment);

                writeMetaboliteInCompartmentBuilder(metabolite.getId(), compartmentBuilder);
            }
        }
    }

    private StringBuilder getCompartmentBuilder(String compartment) {
        StringBuilder compartmentBuilder = compartmentsBuilders.computeIfAbsent(compartment, k -> new StringBuilder());

        return compartmentBuilder;
    }


    private void writeMetaboliteInCompartmentBuilder(String metaboliteID, StringBuilder compartmentFile){
        stringUtils
                .setString(metaboliteID)
                .addSingleQuotes()
                .addBreakLine();

        compartmentFile.append(stringUtils.GetString());
    }

    public void WriteInLowBound(String s){
        loBoundsFileBuilder.append(s);
    }


}
