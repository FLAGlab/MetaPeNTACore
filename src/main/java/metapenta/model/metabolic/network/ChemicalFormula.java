package metapenta.model.metabolic.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChemicalFormula {
	
	private String chemicalFormula;
	
	private Map<String, Integer> elements;
	
	
     /**
	 * Creates a new chemicalFormula
	 * @param chemical formula
	 * @param map of elements and its corresponding stochiometry
	 */
	public ChemicalFormula(String chemicalFormula) {
		super();
		this.chemicalFormula = chemicalFormula;
		Map<String, Integer> elements = getElementsFromFormula(chemicalFormula);
		this.elements = elements;
	}


	public String getChemicalFormula() {
		return chemicalFormula;
	}


	public void setChemicalFormula(String chemicalFormula) {
		this.chemicalFormula = chemicalFormula;
	}


	public Map<String, Integer> getElements() {
		return elements;
	}


	public void setElements(Map<String, Integer> elements) {
		this.elements = elements;
	}
	
	public Map<String, Integer> getElementsFromFormula(String chemformula){
		Map<String, Integer> elements = new HashMap<>();
		
		for (int i = 0; i < chemformula.length(); i++) {
			char e = chemformula.charAt(i);
			
			StringBuilder element = new StringBuilder();
			StringBuilder stoichiom = new StringBuilder();
			Integer stoichiometry = 0;
			int terminaElemen = 0;
			
			if (Character.isLetter(e)){
				element.append(e);
				terminaElemen = i;
				int j = i+1;
				if(j < chemformula.length() && Character.isLowerCase(chemformula.charAt(j))) {
					element.append(chemformula.charAt(j));
					terminaElemen = j;
				}
				int num = terminaElemen+ 1;
				if(num < chemformula.length() && Character.isDigit(chemformula.charAt(num))) {
					stoichiom.append(chemformula.charAt(num));
					Integer sig = num +1;
					if (sig < chemformula.length() && Character.isDigit(chemformula.charAt(sig))) {
						stoichiom.append(chemformula.charAt(sig));
						Integer sig2 = sig +1;
						if (sig2 < chemformula.length() && Character.isDigit(chemformula.charAt(sig2))) {
							stoichiom.append(chemformula.charAt(sig2));
						}
					}	
	            }
				if( num == chemformula.length() || Character.isUpperCase(chemformula.charAt(num))) {
					stoichiom.append('1');
				}

				i = terminaElemen;
				stoichiometry =Integer.parseInt(stoichiom.toString());
				elements.put(element.toString(), stoichiometry);
			}
		}
		
		return elements;
	}
}
