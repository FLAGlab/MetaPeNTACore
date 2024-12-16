package metapenta.model;

import java.util.HashMap;
import java.util.Map;

public class ReactionComponent {
	private Metabolite metabolite;
	private double stoichiometry;
	//Map of elements and abundances scaled by stoichiometry
	private Map<String, Integer> scaledElementsMap;
	
	/**
	 * Creates a reaction component with the given data
	 * @param metabolite that participates in the reaction
	 * @param stoichiometry coefficient of the metabolite in the reaction
	 */
	public ReactionComponent(Metabolite metabolite, double stoichiometry) {
		super();
		this.metabolite = metabolite;
		this.setStoichiometry(stoichiometry);
		
	}
	/**
	 * @return Metabolite that participates in the reaction
	 */
	public Metabolite getMetabolite() {
		return metabolite;
	}
	public String getMetaboliteId() {
		return metabolite.getId();
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
		updateScaledElementsMap();
	}
	public Map<String, Integer> getScaledElementsMap() {
		return scaledElementsMap;
	}
	private void updateScaledElementsMap() {
		ChemicalFormula formula = metabolite.getChemicalFormula();
		if(formula==null) return;
		Map<String, Integer> elements = formula.getElements();
		
		Map<String, Integer> new_elements = new HashMap<>();
		
		for (Map.Entry<String, Integer> entry: elements.entrySet()) {
			new_elements.put(entry.getKey(), (int)(entry.getValue() * this.stoichiometry));
		}
		
		this.scaledElementsMap = new_elements;
	}

	@Override
	public String toString() {
		String JsonReactionComponent="{";
		JsonReactionComponent+="\"metaboliteId\":"+"\""+metabolite.getId()+"\", ";
		JsonReactionComponent+="\"metaboliteName\":"+"\""+metabolite.getName()+"\", ";
		JsonReactionComponent+="\"formula\":"+"\""+metabolite.getChemicalFormula()+"\", ";
		JsonReactionComponent+="\"stoichiometry\":"+"\""+stoichiometry+"\" ";
		JsonReactionComponent+="}";
		return JsonReactionComponent;
	}

}
