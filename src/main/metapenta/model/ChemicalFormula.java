package metapenta.model;

import java.util.HashMap;
import java.util.Map;

public class ChemicalFormula {
	
	private String chemicalFormula;
	
	private Map<String, Integer> elements;
	
	
     /**
	 * Creates a new chemicalFormula
	 * @param chemical formula
	 * @param map of elements and its corresponding stoichiometry
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
		int n = chemicalFormula.length();
		for (int i = 0; i < n; i++) {
			char e = chemicalFormula.charAt(i);
			if (Character.isLetter(e) && Character.isUpperCase(e)){
				StringBuilder element = new StringBuilder();
				StringBuilder stoichiom = new StringBuilder();
				Integer stoichiometry = 0;
				element.append(e);
				int j = i+1;
				for(;j<n;j++) {
					char e2 = chemicalFormula.charAt(j);
					if(!Character.isLetter(e2) || !Character.isLowerCase(e2)) break;
					element.append(e2);
				}
				boolean stFound = false;
				for(;j<n;j++) {
					char e2 = chemicalFormula.charAt(j);
					if(!Character.isDigit(e2)) break;
					stoichiom.append(e2);
					stFound = true;
				}
				if( !stFound) {
					stoichiom.append('1');
				}
				
				try {
					stoichiometry = Integer.parseInt(stoichiom.toString());
				} catch (NumberFormatException e1) {
					throw new IncorrectFormulaException("Error parsing stoichiometry for element: " + element + " in formula "+chemicalFormula, e1);
				}
				String elementStr = element.toString();
				i=j-1;
				//If an element appears twice, there is probably a wildcard
				if(elements.get(elementStr)==null) elements.put(elementStr, stoichiometry);
			} else System.out.println("Ignoring character "+e+" in chemical formula: "+chemicalFormula);
		}
		
		return elements;
	}

	@Override
	public String toString() {
		return chemicalFormula;
	}
	
}
