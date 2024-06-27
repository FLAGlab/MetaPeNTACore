package metapenta.model;

import java.util.HashMap;
import java.util.Map;

public class ChemicalFormula {
	
	private String chemicalFormula;
	
	private Map<String, Integer> elements;
	
	
     /**
	 * Creates a new chemicalFormula
	 * @param chemical formula
	 * @param map of elements and its corresponding stochiometry
	 */
	public ChemicalFormula(String chemicalFormula) throws IncorrectFormulaException {
		super();
		this.setChemicalFormula(chemicalFormula);
		
	}
	
	public String getChemicalFormula() {
		return chemicalFormula;
	}

	private void setChemicalFormula(String chemicalFormula) throws IncorrectFormulaException {
		this.chemicalFormula = chemicalFormula;
		Map<String, Integer> elements = decode();
		this.elements = elements;
	}


	public Map<String, Integer> getElements() {
		return elements;
	}
	
	/**
	 * Decodes the elements from the string representation of this formula
	 * @return Map<String, Integer> map with elements as keys and quantities as values
	 */
	private Map<String, Integer> decode() throws IncorrectFormulaException {
		Map<String, Integer> elements = new HashMap<>();
		
		for (int i = 0; i < chemicalFormula.length(); i++) {
			char e = chemicalFormula.charAt(i);
			
			StringBuilder element = new StringBuilder();
			StringBuilder stoichiom = new StringBuilder();
			Integer stoichiometry = 0;
			int terminaElemen = 0;
			//TODO: Load formulas with wildcards
			if (Character.isLetter(e)){
				element.append(e);
				terminaElemen = i;
				int j = i+1;
				if(j < chemicalFormula.length() && Character.isLowerCase(chemicalFormula.charAt(j))) {
					element.append(chemicalFormula.charAt(j));
					terminaElemen = j;
				}
				int num = terminaElemen+ 1;
				if(num < chemicalFormula.length() && Character.isDigit(chemicalFormula.charAt(num))) {
					stoichiom.append(chemicalFormula.charAt(num));
					Integer sig = num +1;
					if (sig < chemicalFormula.length() && Character.isDigit(chemicalFormula.charAt(sig))) {
						stoichiom.append(chemicalFormula.charAt(sig));
						Integer sig2 = sig +1;
						if (sig2 < chemicalFormula.length() && Character.isDigit(chemicalFormula.charAt(sig2))) {
							stoichiom.append(chemicalFormula.charAt(sig2));
						}
					}	
	            }
				if( num == chemicalFormula.length() || Character.isUpperCase(chemicalFormula.charAt(num))) {
					stoichiom.append('1');
				}

				i = terminaElemen;

				try {
					stoichiometry = Integer.parseInt(stoichiom.toString());
				} catch (NumberFormatException e1) {
					throw new IncorrectFormulaException("Error parsing stoichiometry for element: " + element + " in formula "+chemicalFormula, e1);
				}
				elements.put(element.toString(), stoichiometry);
			}
		}
		
		return elements;
	}

	@Override
	public String toString() {
		return chemicalFormula;
	}
	
}
