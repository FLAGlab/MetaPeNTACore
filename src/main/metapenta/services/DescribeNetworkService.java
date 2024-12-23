package metapenta.services;

import metapenta.model.ChemicalFormula;
import metapenta.model.GeneProduct;
import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.model.ReactionGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DescribeNetworkService {

    private static final String COMPARTMENT_FILE_SUFFIX = "metabolites.txt";
    private MetabolicNetwork network;

    private ByteArrayOutputStream metabolitesOS = new ByteArrayOutputStream();

    private ByteArrayOutputStream reactionsOS = new ByteArrayOutputStream();
    
    private ByteArrayOutputStream reactionGroupsTableOS = new ByteArrayOutputStream();
    
    private ByteArrayOutputStream statisticsOS = new ByteArrayOutputStream();
    
    private StringBuilder sMatrixBuilder = new StringBuilder();

    private StringBuilder reversibleRxnBuilder = new StringBuilder();

    private StringBuilder irreversibleRxnBuilder = new StringBuilder();

    private StringBuilder upBoundFileBuilder = new StringBuilder();

    private StringBuilder loBoundsFileBuilder = new StringBuilder();

    private Map<String, StringBuilder> compartmentsBuilders = new HashMap<>();

    private String metabolitesFileName;

    private String reactionsFile;
    
    private String reactionGroupsTableFile;
    
    private String statisticsFile;

    private String filesPrefix;
    
    private String sMatrixFile;

    private String reversibleRxnFile;

    private String irreversibleRxnFile;

    private String upBoundFile;

    private String loBoundsFile;

    public DescribeNetworkService(MetabolicNetwork network, String prefix){
        this.network = network;
        initFilePrefixes(prefix);
    }

    private void initFilePrefixes(String prefix) {
        this.metabolitesFileName = prefix + "_compounds.txt";
        this.reactionsFile = prefix + "_reactions.txt";
        this.reactionGroupsTableFile = prefix + "_reactionGroupsTable.txt";
        this.statisticsFile = prefix + "_stats.txt";
        this.filesPrefix = prefix;
        
        this.sMatrixFile = prefix + "_s_matrix.txt";
        this.reversibleRxnFile = prefix + "_reversible_rxn.txt";
        this.upBoundFile = prefix + "_upperbounds_on_fluxes.txt";
        this.loBoundsFile = prefix + "_lowerbounds_on_fluxes.txt";
        this.irreversibleRxnFile = prefix + "_irreversible_reactions.txt";

    }

    public void write() throws IOException {
        prepareFiles();
        writeFiles();
    }

    private void prepareFiles() {
        
        prepareMetabolitesFile();
        prepareReactionsFile();
        prepareReactionGroupsTable();
        prepareStatistics();
        
        //prepareSMatrix();
        //prepareCompartmentsFiles();
        //prepareReversibleReactions();
        //prepareIrreversibleReactions();
        
    }

	

	private void writeFiles() throws IOException {
        Files.write(Paths.get(metabolitesFileName), metabolitesOS.toByteArray());
        Files.write(Paths.get(reactionsFile), reactionsOS.toByteArray());
        Files.write(Paths.get(reactionGroupsTableFile), reactionGroupsTableOS.toByteArray());
        Files.write(Paths.get(statisticsFile), statisticsOS.toByteArray());

        //Files.write(Paths.get(sMatrixFile), sMatrixBuilder.toString().getBytes());
        //Files.write(Paths.get(reversibleRxnFile), reversibleRxnBuilder.toString().getBytes());
        //Files.write(Paths.get(irreversibleRxnFile), irreversibleRxnBuilder.toString().getBytes());
        //Files.write(Paths.get(upBoundFile), upBoundFileBuilder.toString().getBytes());
        //Files.write(Paths.get(loBoundsFile), loBoundsFileBuilder.toString().getBytes());
        //writeCompartmentFiles();
        
    }

    

    private void prepareMetabolitesFile(){
    	PrintStream out = new PrintStream(metabolitesOS);
        List<Metabolite> metabolites = network.getMetabolitesAsList();
        for(Metabolite m:metabolites){
        	out.print(m.getId()+"\t"+m.getName()+"\t"+m.getCompartmentId());
        	ChemicalFormula f = m.getChemicalFormula();
        	out.print("\t"+((f!=null)?m.getChemicalFormula():"None"));
        	out.print("\t"+m.getCharge());
        	out.println();
        }
    }

    private void prepareReactionsFile(){
    	PrintStream out = new PrintStream(reactionsOS);
    	List<Reaction> reactions = network.getReactionsAsList();
    	for(Reaction r: reactions) {
    		List<GeneProduct> geneProducts = r.getEnzymes();
        	StringBuilder builder1 = new StringBuilder();
        	StringBuilder builder2 = new StringBuilder();
        	if(geneProducts!=null) {
        		for(GeneProduct product:geneProducts) {
        			if(builder1.length()>0) builder1.append(",");
        			builder1.append(product.getId());
        			if(builder2.length()>0) builder2.append(",");
        			builder2.append(product.getName());
        		}
        	}
        	String enzymeIds="NONE";
        	String enzymeNames="NONE";
        	if(builder1.length()>0) {
        		enzymeIds = builder1.toString();
        		enzymeNames = builder2.toString();
        	}
            String answer = r.getId()+"\t"+r.getKeggId()+"\t"+enzymeIds+"\t"+enzymeNames+"\n";

            out.append(answer);
    	}
    }
   
    
    private void prepareReactionGroupsTable() {
    	PrintStream out = new PrintStream(reactionGroupsTableOS);
		Map<String, ReactionGroup> reactionGroupsMap = network.getReactionGroups();
		List<ReactionGroup> reactionGroupsList = new ArrayList<>(reactionGroupsMap.values());
		Collections.sort(reactionGroupsList,(g1,g2)->g1.getName().compareTo(g2.getName()));
		for(ReactionGroup group:reactionGroupsList) {
			out.print(group.getId()+"\t"+group.getName());
			for(String rID:group.getReactionIds()) { 
				out.print("\t"+rID);
			}
			out.println();
		}	
	}
    
    private void prepareStatistics() {
    	PrintStream out = new PrintStream(statisticsOS);
		out.println("Metabolites: "+network.getMetabolitesAsList().size());
		List<Reaction> reactions = network.getReactionsAsList();
		out.println("Reactions: "+ reactions.size());
		out.println("Gene products: "+network.getGeneProductsAsList().size());
		Map<String, ReactionGroup> reactionGroupsMap = network.getReactionGroups();
		List<ReactionGroup> reactionGroupsList = new ArrayList<>(reactionGroupsMap.values());
		out.println("Reaction groups: "+reactionGroupsList.size());
		Collections.sort(reactionGroupsList,(g1,g2)->g1.getName().compareTo(g2.getName()));
		out.println("ID\tName\tReactions\tGeneProducts\tGPR");
		for(ReactionGroup group:reactionGroupsList) {
			List<Reaction> reactionsGroup = group.getReactions();
			int nR = reactionsGroup.size();
			Set<String> geneProductIds = new HashSet<>();
			int gpr = 0;
			for(Reaction r:reactionsGroup) {
				List<GeneProduct> enzymes = r.getEnzymes(); 
				gpr+=enzymes.size();
				for(GeneProduct gp:enzymes) {
					geneProductIds.add(gp.getId());
				}
			}
			out.println(group.getId()+"\t"+group.getName()+"\t"+nR+"\t"+geneProductIds.size()+"\t"+gpr);
		}
	}

    private void prepareCompartmentsFiles() {
        Map<String, List<Metabolite>> metabolitesByCompartments = network.getMetabolitesByCompartments();
        Set<String> compartments = metabolitesByCompartments.keySet();

        for(String compartment: compartments) {
            List<Metabolite> metabolites = metabolitesByCompartments.get(compartment);
            for(Metabolite metabolite: metabolites) {
                StringBuilder compartmentBuilder = getCompartmentBuilder(compartment);
                compartmentBuilder.append(metabolite.getId());
            }
        }
    }
    
    private void writeCompartmentFiles() throws IOException {
        Set<String> compartments = compartmentsBuilders.keySet();

        for(String compartment: compartments) {
            StringBuilder compartmentBuilder = compartmentsBuilders.get(compartment);
            Files.write(Paths.get(filesPrefix + "_"+compartment + "_"+COMPARTMENT_FILE_SUFFIX), compartmentBuilder.toString().getBytes());
        }
    }

    private void prepareSMatrix() {
    	List<Reaction> reactions = network.getReactionsAsList();
    	for(Reaction reaction: reactions) {
            writeReactionComponentList(reaction.getReactants(), 1);
            writeReactionComponentList(reaction.getProducts(), -1);
        }

    }

    private void writeReactionComponentList(List<ReactionComponent> reactionComponents, double stoichiometryMultiplier) {
        for (ReactionComponent product: reactionComponents){
        	sMatrixBuilder.append(product.getMetaboliteId()+" "+(stoichiometryMultiplier * product.getStoichiometry()));
        }
    }

    private StringBuilder getCompartmentBuilder(String compartment) {
        return compartmentsBuilders.computeIfAbsent(compartment, k -> new StringBuilder());
    }

    /**
	 * args[0]: Metabolic network file
	 * args[1]: Output file prefixes
	 */
    public static void main(String[] args) throws Exception {
        DescribeNetworkService networkWriter = new DescribeNetworkService(MetabolicNetwork.load(args[0]), args[1]);
        networkWriter.write();
    }

}
