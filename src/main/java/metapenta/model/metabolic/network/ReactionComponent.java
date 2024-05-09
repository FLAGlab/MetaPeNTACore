package metapenta.model.metabolic.network;

import java.util.HashMap;
import java.util.Map;

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
		updateFormulaReactionComponent();
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
	public void updateFormulaReactionComponent() {
		ChemicalFormula formula = metabolite.getChemicalFormula();
		if(formula==null) {
			//TODO: Decide if an empty map should be created
			//System.err.println("WARN. Problem updating formula for metabolite "+metabolite.getId());
			return;
		}
		Map<String, Integer> elements = formula.getElements();
		
		Map<String, Integer> new_elements = new HashMap<>();
		
		for (Map.Entry<String, Integer> entry: elements.entrySet()) {
			new_elements.put(entry.getKey(), (int)(entry.getValue() * this.stoichiometry));
		}
		
		this.formulaReactionComponent = new_elements;
	}

	public void setMetabolite(Metabolite metabolite) {
		this.metabolite = metabolite;
	}

	public String getMetaboliteID() {
		return metabolite.ID();
	}
	@Override
	public String toString() {
		String JsonReactionComponent="{";
		JsonReactionComponent+="\"metaboliteId\":"+"\""+metabolite.ID()+"\", ";
		JsonReactionComponent+="\"metaboliteName\":"+"\""+metabolite.getName()+"\", ";
		JsonReactionComponent+="\"stoichiometry\":"+"\""+stoichiometry+"\" ";
		JsonReactionComponent+="}";
		return JsonReactionComponent;
	}

}
