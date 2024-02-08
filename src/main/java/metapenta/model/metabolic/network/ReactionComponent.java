package metapenta.model.metabolic.network;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents the data related to a metabolite within a reaction
 * @author Jorge Duitama
 *
 */
public class ReactionComponent {
	private Metabolite metabolite;
	private double stoichiometry;
	private Map<String, Integer> formulaReactionComponent;
	
	/**
	 * Creates a reaction component with the given data
	 * @param metabolite that participates in the reaction
	 * @param stoichiometry coefficient of the metabolite in the reaction
	 */
	public ReactionComponent(Metabolite metabolite, double stoichiometry) {
		super();
		this.metabolite = metabolite;
		this.stoichiometry = stoichiometry;
	}
	/**
	 * @return Metabolite that participates in the reaction
	 */
	public Metabolite getMetabolite() {
		return metabolite;
	}
	/**
	 * 
	 * @return steichiometry coefficient of the metabolite within the reaction
	 */
	public double getStoichiometry() {
		return stoichiometry;
	}
	
	
	public void setStoichiometry(double stoichiometry) {
		this.stoichiometry = stoichiometry;
	}
	public Map<String, Integer> getFormulaReactionComponent() {
		return formulaReactionComponent;
	}
	public void setFormulaReactionComponent(Metabolite metabolite) {
		ChemicalFormula formula = metabolite.getChemicalFormula();
		Map<String, Integer> elements = formula.getElements();
		
		Map<String, Integer> new_elements = new HashMap<>();
		
		for (Map.Entry<String, Integer> entry: elements.entrySet()) {
			new_elements.put(entry.getKey(), (int)(entry.getValue() * this.stoichiometry));
		}
		
		this.formulaReactionComponent = new_elements;
	}
	
	@Override
	public String toString() {
		String JsonReactionComponent="{";
		JsonReactionComponent+="\"metaboliteId\":"+"\""+metabolite.getId()+"\", ";
		JsonReactionComponent+="\"metaboliteName\":"+"\""+metabolite.getName()+"\", ";
		JsonReactionComponent+="\"stoichiometry\":"+"\""+stoichiometry+"\" ";
//		JsonReactionComponent+="\"detailFormula\":"+"\""+detailFormula+"\" ";
//		JsonReactionComponent+="\"formulaReactionComponent\":"+"\""+formulaReactionComponent+"\" ";
		JsonReactionComponent+="}";
		return JsonReactionComponent;
	}
	
	
}