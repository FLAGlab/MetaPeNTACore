package metapenta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * Represents a reaction between metabolites
 * @author Jorge Duitama
 */
public class Reaction {
	private int nid;
	private String id;
	private String name;
	private String sboTerm;
	private List<ReactionComponent> reactants;
	private List<ReactionComponent> products;
	private boolean reversible = false;
	private String lowerBoundFluxParameterId;
	private String upperBoundFluxParameterId;
	private double lowerBoundFlux = -1000;
	private double upperBoundFlux = 1000;
	private List<GeneProduct> enzymes;
	private boolean balanced = false;
	private List<String> links;

	public Reaction(){}
	public Reaction(String id){
		this.id = id;
	}
	/**
	 * Creates a new reaction with the given information
	 * @param id of the reaction
	 * @param name of the reaction
	 * @param reactants Metabolites that serve as input of the reaction
	 * @param products Metabolites that serve as output of the reaction
	 */
	public Reaction(String id, String name, List<ReactionComponent> reactants, List<ReactionComponent> products, int nid) {
		super();
		this.id = id;
		this.name = name;
		this.reactants = reactants;
		this.products = products;
		this.nid = nid;
		updateBalanced();
	}

	public Reaction(String id, String name, List<ReactionComponent> reactants, List<ReactionComponent> products) {
		super();
		this.id = id;
		this.name = name;
		this.reactants = reactants;
		this.products = products;
		updateBalanced();
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSboTerm() {
		return sboTerm;
	}

	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
	}

	public void setProducts(List<ReactionComponent> products) {
		this.products = products;
		updateBalanced();
	}

	public void setReactants(List<ReactionComponent> reactants) {
		this.reactants = reactants;
		updateBalanced();
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return true if the reaction is reversible, false otherwise
	 */
	public boolean isReversible() {
		return reversible;
	}
	/**
	 * Changes the reversible status
	 * @param reversible new reversible status
	 */
	public void setReversible(boolean reversible) {
		this.reversible = reversible;
	}

	/**
	 * @return id of the reaction
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return name of the reaction
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return List of input metabolites
	 */
	public List<ReactionComponent> getReactants() {
		return reactants;
	}
	/**
	 * @return List of output metabolites
	 */
	public List<ReactionComponent> getProducts() {
		return products;
	}
	/**
	 * @return List<GeneProduct> Enzymes that catalyze the reaction
	 */
	public List<GeneProduct> getEnzymes() {
		return enzymes;
	}
	/**
	 * Changes the enzymes that catalyze the reaction
	 * @param enzymes New enzymes
	 */
	public void setEnzymes(List<GeneProduct> enzymes) {
		this.enzymes = enzymes;
	}
	/**
	 * @return Lower bound of the flux in this reaction
	 */
	public double getLowerBoundFlux() {
		return lowerBoundFlux;
	}
	/**
	 * Changes the lower bound of the flux in this reaction
	 * @param lowerBound new lower bound
	 */
	public void setLowerBoundFlux(double lowerBound) {
		this.lowerBoundFlux = lowerBound;
	}
	/**
	 * @return Upper bound of the flux in this reaction
	 */
	public double getUpperBoundFlux() {
		return upperBoundFlux;
	}
	/**
	 * Changes the upper bound of the flux in this reaction
	 * @param upperBound new upper bound
	 */
	public void setUpperBoundFlux(double upperBound) {
		this.upperBoundFlux = upperBound;
	}
	
	public String getLowerBoundFluxParameterId() {
		return lowerBoundFluxParameterId;
	}
	public void setLowerBoundFluxParameterId(String lowerBoundFluxParameterId) {
		this.lowerBoundFluxParameterId = lowerBoundFluxParameterId;
	}
	public String getUpperBoundFluxParameterId() {
		return upperBoundFluxParameterId;
	}
	public void setUpperBoundFluxParameterId(String upperBoundFluxParameterId) {
		this.upperBoundFluxParameterId = upperBoundFluxParameterId;
	}
	public boolean isBalanced() {
		return balanced;
	}
	private void setBalanced(boolean balanced) {
		this.balanced = balanced;
	}
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
	public void addProduct(ReactionComponent product) {
		products.add(product);
		updateBalanced();
	}
	/**
	 * Method that makes a String with the information about the reactants
	 * of the Reaction
	 * @return reactantSring
	 */
	private String printReactants() {
		String reactantSring="";

		for (int i = 0; i < reactants.size(); i++) {
			if(i==reactants.size()-1) {
				reactantSring+=reactants.get(i).toString();
			}
			else {
				reactantSring+=reactants.get(i).toString()+",";
			}			
		}
		return reactantSring;
	}
	
	/**
	 * Calculates the sums of scaled abundances of elements for the given reaction components
	 * @param reactionComponents list of components to sum
	 * @return Map<String, Integer> Map with elements as keys and total abundances as values
	 */
	private Map<String, Integer> getTotalScaledElementsMap(List<ReactionComponent> reactionComponents) {
		Map<String, Integer> totals = new HashMap<String, Integer>();
		for(ReactionComponent component: reactionComponents) {
			Map<String, Integer> elements = component.getScaledElementsMap();
			if(elements==null) return null;
			for(Map.Entry<String, Integer> entry:elements.entrySet()) {
				totals.compute(entry.getKey(), (k,v)-> (v==null)?entry.getValue():v+entry.getValue());
			}
		}
		return totals;
	}

	public Map<String, Integer> getSumReactants() {
		return getTotalScaledElementsMap(reactants);
	}
	public Map<String, Integer> getSumProducts() {
		return getTotalScaledElementsMap(products);
	}

	private void updateBalanced() {
		Map<String, Integer> sumlistElemReactants = getSumReactants();
		Map<String, Integer> sumlistElemProducts = getSumProducts();
		if(sumlistElemReactants==null || sumlistElemProducts==null) balanced = false;
		else balanced = sumlistElemReactants.equals(sumlistElemProducts);

	}

	public Map<String, Integer> getDifference() {
        Map<String, Integer> sumlistElemReactants = getSumReactants();
        Map<String, Integer> sumlistElemProducts = getSumProducts();
        if(sumlistElemReactants==null || sumlistElemProducts==null) return null;
        Map<String, Integer> difference = new HashMap<>();
        Set<String> allElements = new HashSet<String>();
        allElements.addAll(sumlistElemReactants.keySet());
        allElements.addAll(sumlistElemProducts.keySet());

        for (String key : allElements) {
        	int reactantValue = sumlistElemReactants.getOrDefault(key, 0);
            int productValue = sumlistElemProducts.getOrDefault(key, 0);

            if (reactantValue - productValue != 0) {
                difference.put(key, reactantValue - productValue);
            }
        }
        System.out.println("Sum reactants: "+sumlistElemReactants);
        System.out.println("Sum products: "+sumlistElemProducts);
        System.out.println("Differences: "+difference);
        return difference;
    }

	/**
	 * Assess why the reaction is not balanced
	 * @return String with assessment for unbalanced reactions
	 */
	public String findReasonNotBalanced() {
		
		if(balanced) return "Reaction is balanced";
		Map<String, Integer> sumlistElemReactants = getSumReactants();
		Map<String, Integer> sumlistElemProducts = getSumProducts();
		if(sumlistElemReactants == null) return "At least one reactant does not have chemical formula ";
		if(sumlistElemProducts == null) return "At least one product does not have chemical formula ";
		String reason = "";
		String sumreactions = "";
		if(!sumlistElemReactants.keySet().equals(sumlistElemProducts.keySet())) {
			reason = "Reactants and product do not have the same elements ";
		} else {
			reason = "The sum of coefficients is different on both sides";
		}
		//sumreactions = "Sum of stoichiometric coefficients(reactants): ";
        for (Map.Entry<String, Integer> entry : sumlistElemReactants.entrySet()) {
        	sumreactions = sumreactions + "{ "+entry.getKey() + ": " + entry.getValue()+ "}";
        }
        sumreactions = sumreactions + " | ";
        //reason = reason + "Sum of stoichiometric coefficients(products): ";
        for (Map.Entry<String, Integer> entry : sumlistElemProducts.entrySet()) {
        	sumreactions = sumreactions + "{ "+entry.getKey() + ": " + entry.getValue()+ "} ";
        }
        sumreactions = sumreactions + " | ";
        Map<String, Integer> difference = getDifference();
        //reason = reason + "Difference between reactants and products: ";
        for (Map.Entry<String, Integer> entry : difference.entrySet()) {
        	sumreactions = sumreactions + "{ "+entry.getKey() + ": " + entry.getValue()+ "}";
        }
        sumreactions = sumreactions + " | ";
        reason+=" "+sumreactions;
		return reason;

	}
	//TODO: Move to another class
	private double[][] linarSystem(){
		double[][] ecuaciones = new double[getSumReactants().size()][getSumReactants().size()];

		Set<String> elements = getSumReactants().keySet();
		ArrayList<String> elementsList = new ArrayList<>(elements);
		for(int e = 0; e < ecuaciones.length; e++) {
			String element = elementsList.get(e);

			double[] lineaElem = new double[getSumReactants().size()];
			for (int i = 0; i < lineaElem.length; i++) {
				if(i < reactants.size()) {
					ReactionComponent react = reactants.get(i);
					Metabolite m = react.getMetabolite();
					ChemicalFormula formula = m.getChemicalFormula();
					Map<String, Integer> elems = formula.getElements();

					if (elems.containsKey(element)) {
						Integer coeff = elems.get(element);
						lineaElem[i] = coeff;
					}

				}
				else {
					if(i < reactants.size()+ products.size()) {
						ReactionComponent product = products.get(i - reactants.size());
						Metabolite m = product.getMetabolite();
						ChemicalFormula formula = m.getChemicalFormula();
						Map<String, Integer> elems = formula.getElements();

						if (elems.containsKey(element)) {
							Integer coeff = elems.get(element);
							lineaElem[i] = -coeff;
						}
					}
				}

			}

			for (int i = 0; i < lineaElem.length; i++) {
				ecuaciones[e][i] =lineaElem[i];
			}
		}
		return ecuaciones;

	}
	//TODO: Move to another class
	private double[] solutionSytem(double[][] ecuations){

		double[] solution_coeff = new double[getSumReactants().size()];

		try {
		RealMatrix coefficients = new Array2DRowRealMatrix(ecuations,
                false);

		double result[][] = new double[1][1];
	    result[0][0] = (new LUDecomposition(coefficients)).getDeterminant();
	    System.out.println(result[0][0]);
	    if(result[0][0]!= 0) {
	    	DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
			RealVector constants = new ArrayRealVector(solution_coeff, false);
			RealVector solution = solver.solve(constants);

			for (int i = 0; i < getSumReactants().size(); i++) {
				solution_coeff[i] =solution.getEntry(i);
			}
	    } } catch (Exception e) {
	        // Manejar otras excepciones si es necesario
	        e.printStackTrace();
	    }
		return solution_coeff;
	}

	/**
	 * Balances the reaction
	 * @return String Description of the change or reasoning if the reaction was not balanced
	 */
	public String balanceReaction() {
		Map<String, Integer> sumlistElemReactants = getSumReactants();
		Map<String, Integer> sumlistElemProducts = getSumProducts();
		if(sumlistElemReactants == null) return "At least one reactant does not have chemical formula ";
		if(sumlistElemProducts == null) return "At least one product does not have chemical formula ";
		if(!sumlistElemReactants.keySet().equals(sumlistElemProducts.keySet())) return "Reactants and product do not have the same elements ";
		Map<String, Integer> difference = getDifference();
		List<Reaction> reactionsBalanced = new ArrayList<>();
		int mcm = 0;
		String answer = "";

		if (reactants.size() == 1 && products.size()==1) {
			ReactionComponent reactant = reactants.get(0);
			ReactionComponent product = products.get(0);
			if(reactant.getStoichiometry() == 1 && product.getStoichiometry() == 1) {
				Map<String, Integer> formulaReactant = reactant.getScaledElementsMap();
				Map<String, Integer> formulaProduct = product.getScaledElementsMap();
				int countSame = 0;
				//TODO: Check inconsistencies in this flag
				String mayor = "";

				for (String element : formulaReactant.keySet()) {
					int numReactant = formulaReactant.get(element);
					int numProduct = formulaProduct.get(element);
					int a = Math.max(numReactant, numProduct);
					int b = Math.min(numReactant, numProduct);
					if (a == numReactant) {
						mayor = "reactant";
					} else {
						mayor = "product";
					}
					int mcmReactProduct = minimoComunMultiplo(a, b);
					if(mcm == 0) {
						mcm = mcmReactProduct;
						countSame++;
					} else if(mcmReactProduct != mcm) {
		                setBalanced(false);
		                answer = "Inconsistent MCM";
		                break;
		            } else {
		            	countSame++;
		            }
				}
				if(countSame == formulaReactant.keySet().size()) {
					if(mayor.equals("reactant")) {
						reactant.setStoichiometry(mcm);
						if(isBalanced()) {
							setBalanced(true);
							reactionsBalanced.add(this);
							answer = "Modified stochiometry by MCM";
							//changedToBalanced = true;
						}
					}
				}
			}
		} else if(difference.size() == 1) {
			//TODO: check this code
			for (Map.Entry<String, Integer> entry : difference.entrySet()) {
				String elem = entry.getKey();
				Integer differenceNum = entry.getValue();

				if (differenceNum < 0) {
					outerLoop1:
						for(ReactionComponent react : reactants) {
							Map<String, Integer> chemicalFormula = react.getScaledElementsMap();
							if(chemicalFormula.size() == 1) {
								for (Map.Entry<String, Integer> formula : chemicalFormula.entrySet()) {
									if(formula.getKey().equals(elem)) {
										Integer newStoich = (int) (Math.abs(differenceNum) + react.getStoichiometry());
										react.setStoichiometry(Math.abs(newStoich));
										react.setStoichiometry(differenceNum);
										reactionsBalanced.add(this);
										answer = "Added atoms of one element in reactans";
										break outerLoop1;
									}
								}
							}
						}
				} else {
				outerLoop2:
				for(ReactionComponent product: products) {
					Map<String, Integer> chemicalFormula = product.getScaledElementsMap();
					if(chemicalFormula.size() == 1) {
						for (Map.Entry<String, Integer> formula : chemicalFormula.entrySet()) {
							if(formula.getKey().equals(elem)) {
								System.out.println("nueva Stoichiometry");
								System.out.println(differenceNum);
								Integer newStoich = (int) (differenceNum + product.getStoichiometry());
								product.setStoichiometry(Math.abs(newStoich));

								//System.out.println(m.getName());
								
								Map<String, Integer> formulacambiada = product.getScaledElementsMap();
								for (Map.Entry<String, Integer> formul : formulacambiada.entrySet()) {
									System.out.println("FORMULA CAMBIADA");
									System.out.println(formul.getKey() + ":" + formul.getValue());
								}
								reactionsBalanced.add(this);
								answer = "Added atoms of one element in products";
								break outerLoop2;
							}
						}

					}
				}
			}

			} 
		} else if((reactants.size()+ products.size()) <= sumlistElemReactants.size()) {
			double[][] ecuaciones = linarSystem();
			for (double[] filaEcuacion : ecuaciones) {
	            for (double valor : filaEcuacion) {
	                System.out.print(valor + " ");
	            }
	            System.out.println();
	        }
			//TODO: Change to int
			double[] solution = solutionSytem(ecuaciones);

			System.out.println("SOLUCION");
			for (int i = 0; i < solution.length; i++) {
				System.out.println(solution[i]);
			}

			boolean allZeros = true;
			for (double element : solution) {
	            if (element != 0.0) {
	                allZeros = false;
	                break;
	            }
	        }
			if(allZeros) {
				answer = "Undefined solution by system of equations";
			}
			else {
				answer = "Solution by system of equations";
				int multiplicador = changeDoubleIntResult(solution);
				int[] resultFinal = new int[solution.length];
				for (int i = 0; i < solution.length; i++) {
					resultFinal[i] = (int)Math.round(solution[i] * multiplicador);
		        }

				for (int i = 0; i < resultFinal.length; i++) {
					if(i < reactants.size()) {
						ReactionComponent react = reactants.get(i);
						react.setStoichiometry(resultFinal[i]);

					}
					else {
						if(i < reactants.size()+ products.size()) {
							ReactionComponent product = products.get(i - reactants.size());
							product.setStoichiometry(resultFinal[i]);

						}
					}
				}

			}
		}
		else if((reactants.size()+ products.size()) > sumlistElemReactants.size()) {
			answer = "Impossible to solve by a system of equations";
		}
		return answer;
	}

	public static int changeDoubleIntResult(double[] numeros) {
        int multiplicador = (int) numeros[0];

        for (int i = 1; i < numeros.length; i++) {
            multiplicador = multiplicador * (int) numeros[i] / maximoComunDivisor(multiplicador, (int) numeros[i]);
        }

        return multiplicador;
    }

	public static int maximoComunDivisor(int a, int b) {
        int temporal;
        while (b != 0) {
            temporal = b;
            b = a % b;
            a = temporal;
        }
        return a;
    }

    public static int minimoComunMultiplo(int a, int b) {
        // MCM(a, b) = (a * b) / MCD(a, b)
        return (a * b) / maximoComunDivisor(a, b);
    }
	/**
	 * Method that makes a String with the information about the products
	 * of the reaction
	 * of the Reaction
	 * @return A string with the information of the reaction
	 */
	private String printProducts() {
		String productString="";

		for (int i = 0; i < products.size(); i++) {
			if(i==products.size()-1) {
				productString+=products.get(i).toString();
			}
			else {
				productString+=products.get(i).toString()+",";
			}			
		}
		return productString;
	}

	public int getNid() {
		return nid;
	}


	@Override
	public String toString() {
		String jsonReaction="{";
		jsonReaction+=" \"id\":"+"\""+id+"\",";
		jsonReaction+=" \"name\":"+"\""+name+"\",";
		jsonReaction+=" \"reversible\":"+reversible+",";
		jsonReaction+=" \"reactants\":[";
		jsonReaction+= printReactants()+" ],";
		jsonReaction+=" \"products\":[";
		jsonReaction+= printProducts()+" ]";
		jsonReaction+="}";
		return jsonReaction;
	}
	
	public Reaction clone() {
		return new Reaction(id, name, reactants, products, nid);
	}

	public Set<String> getAllComponentsIDs() {
		Set<String> componentsIDs = new HashSet<>();
		for(ReactionComponent reactant: reactants) {
			componentsIDs.add(reactant.getMetabolite().getId());
		}
		for(ReactionComponent product: products) {
			componentsIDs.add(product.getMetabolite().getId());
		}
		return componentsIDs;
	}

}
