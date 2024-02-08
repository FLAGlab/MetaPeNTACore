package metapenta.model.metabolic.network;

import org.apache.commons.math3.linear.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a reaction between metabolites
 * @author Jorge Duitama
 */
public class Reaction {
	private int nid;
	private String id;
	private String name;
	private List<ReactionComponent> reactants;
	private List<ReactionComponent> products;
	private boolean reversible = false;
	private double lowerBoundFlux = -1000;
	private double upperBoundFlux = 1000;
	private List<GeneProduct> enzymes;
	private Map<String, Integer> stoichiometryDifference;
	private boolean isBalanced = false;
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
		if (isBalanced()) {
			this.isBalanced = true;
		}
		this.nid = nid;
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


	public boolean getIsBalanced() {
		return isBalanced;
	}
	public void setIsBalanced(boolean isBalanced) {
		this.isBalanced = isBalanced;
	}

	public void addProduct(ReactionComponent product) {
		products.add(product);
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

	public List<Map<String, Integer>> getListElements(List<ReactionComponent> reactionsComponent) {
		List<Map<String, Integer>> listElements = new ArrayList<>();
		for(ReactionComponent reaction: reactionsComponent) {
			Map<String, Integer> elements = reaction.getFormulaReactionComponent();
			listElements.add(elements);
		}
			return listElements;
	}

	public Map<String, Integer> getSumReactants() {
		List<Map<String, Integer>> listElements = getListElements(reactants);
		Map<String, Integer> sum = listElements.stream().flatMap(elements -> elements.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,Integer::sum));
		return sum;
	}
	public Map<String, Integer> getSumProducts() {
		List<Map<String, Integer>> listElements = getListElements(products);
		Map<String, Integer> sum = listElements.stream().flatMap(elements -> elements.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,Integer::sum));
		return sum;
	}

	public boolean isBalanced() {
		Map<String, Integer> sumlistElemReactants = getSumReactants();

		Map<String, Integer> sumlistElemProducts = getSumProducts();

		return sumlistElemReactants.equals(sumlistElemProducts);

	}




	public Map<String, Integer> getDifference() {


        Map<String, Integer> sumlistElemReactants = getSumReactants();

        Map<String, Integer> sumlistElemProducts = getSumProducts();

        Map<String, Integer> difference = new HashMap<>();

        for (String key : sumlistElemReactants.keySet()) {
        	int reactantValue = sumlistElemReactants.getOrDefault(key, 0);
            int productValue = sumlistElemProducts.getOrDefault(key, 0);

            if (reactantValue - productValue != 0) {
                difference.put(key, reactantValue - productValue);
            }
        }
        System.out.println("suma reactantes");
        for (Map.Entry<String, Integer> entry : sumlistElemReactants.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("suma productos");
        for (Map.Entry<String, Integer> entry : sumlistElemProducts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("DIFERENCIA");
        for (Map.Entry<String, Integer> entry : difference.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        return difference;
    }

	public Map<String, String> casesNoBalanced() {

		String reason = "";
		String sumreactions = "";
		Map<String, String> reasonSum = new HashMap<>();

		List<Map<String, Integer>> listElemReactants = getListElements(reactants);

		List<Map<String, Integer>> listElemProducts = getListElements(products);

		Map<String, Integer> sumlistElemReactants = getSumReactants();

		Map<String, Integer> sumlistElemProducts = getSumProducts();



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


		for(Map<String, Integer> elementReactants: listElemReactants) {
			if(elementReactants == null) {
				reason = "At least one reactant does not have chemical formula ";
			}
		}
		for(Map<String, Integer> elementProducts: listElemProducts) {
			if(elementProducts == null) {
				reason = "At least one product does not have chemical formula ";
			}
		}
		if(reason.equals("")) {
			if(sumlistElemReactants.size() != sumlistElemProducts.size()) {
				reason = "Reactants and product do not have the same elements ";
			}
			else {
				reason = "The sum of coefficients is different on both sides";
			}

		}



        reasonSum.put(reason, sumreactions);



		return reasonSum;

	}
	public double[][] linarSystem(){
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

	public double[] solutionSytem(double[][] ecuations){

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
	    }



		} catch (Exception e) {
	        // Manejar otras excepciones si es necesario
	        e.printStackTrace();
	    }

		return solution_coeff;

	}

	public Map<Boolean, String> balanceReaction() {

		List<Map<String, Integer>> listElemReactants = getListElements(reactants);

		List<Map<String, Integer>> listElemProducts = getListElements(products);

		Map<String, Integer> sumlistElemReactants = getSumReactants();

		Map<String, Integer> sumlistElemProducts = getSumProducts();

		Map<String, Integer> diference = getDifference();

		List<Reaction> reactionsBalanced = new ArrayList<>();

		//boolean changedToBalanced = false;

		int mcm = 0;

		Map<Boolean, String> result = new HashMap<>();





		for(Map<String, Integer> elementReactants: listElemReactants) {
			if(elementReactants == null) {
				setIsBalanced(false);
				result.put(false, "");
			}
		}
		for(Map<String, Integer> elementProducts: listElemProducts) {
			if(elementProducts == null) {
				setIsBalanced(false);
				result.put(false, "");
			}
		}

		if(sumlistElemReactants.size() != sumlistElemProducts.size()) {
			setIsBalanced(false);
			result.put(false, "");
		}
		else if (reactants.size() == 1 && products.size()==1) {
			if(reactants.get(0).getStoichiometry() == 1.0 && products.get(0).getStoichiometry() == 1.0) {
				ReactionComponent reactant = reactants.get(0);
				ReactionComponent product = products.get(0);

				Map<String, Integer> formulaReactant = reactant.getFormulaReactionComponent();
				Map<String, Integer> formulaProduct = product.getFormulaReactionComponent();



				if (formulaReactant.keySet().equals(formulaProduct.keySet())) {
					int contIgual = 0;
					String mayor = "";

					for (String element : formulaReactant.keySet()) {

						int numReactant = formulaReactant.get(element);
		                int numProduct = formulaProduct.get(element);

		                int a = Math.max(numReactant, numProduct);
		                int b = Math.min(numReactant, numProduct);

		                if (a == numReactant) {
		                	mayor = "reactant";
		                }else {
		                	mayor = "product";
		                }

		                int mcmReactProduct = minimoComunMultiplo(a, b);

		                if(mcm == 0) {
		                	mcm = mcmReactProduct;
		                	contIgual++;
		                }else {
		                	if(mcmReactProduct != mcm) {
		                		setIsBalanced(false);
		                		result.put(false, "");
		                		break;
		                	}else {
		                		contIgual++;
		                	}
		                }

					}
					if(contIgual == formulaReactant.keySet().size()) {
						if(mayor.equals("reactant")) {
							reactant.setStoichiometry(mcm);
							if(isBalanced()) {
								setIsBalanced(true);
								reactionsBalanced.add(this);
								result.put(true, "Modify stochiometry by MCM");
								//changedToBalanced = true;
							}
						}

					}



				}
			}
		}
		else if(diference.size() == 1) {
			for (Map.Entry<String, Integer> entry : diference.entrySet()) {
				String elem = entry.getKey();
				Integer differenceNum = entry.getValue();

				if (differenceNum < 0) {
					outerLoop1:
						for(ReactionComponent react : reactants) {
							Map<String, Integer> chemicalFormula = react.getFormulaReactionComponent();
							if(chemicalFormula.size() == 1) {
								for (Map.Entry<String, Integer> formula : chemicalFormula.entrySet()) {
									if(formula.getKey().equals(elem)) {
										Integer newStoich = (int) (Math.abs(differenceNum) + react.getStoichiometry());
										react.setStoichiometry(Math.abs(newStoich));
										react.setStoichiometry(differenceNum);
										Metabolite m = react.getMetabolite();
										react.setFormulaReactionComponent(m);
										reactionsBalanced.add(this);
										result.put(true, "Adding atoms of one element in reactans");
										break outerLoop1;
									}
									else {
										result.put(false, "");
									}
								}

							}

						}
				}else {
				outerLoop2:
				for(ReactionComponent product: products) {
					Map<String, Integer> chemicalFormula = product.getFormulaReactionComponent();
					if(chemicalFormula.size() == 1) {
						for (Map.Entry<String, Integer> formula : chemicalFormula.entrySet()) {
							if(formula.getKey().equals(elem)) {
								System.out.println("nueva Stoichiometry");
								System.out.println(differenceNum);
								Integer newStoich = (int) (differenceNum + product.getStoichiometry());
								product.setStoichiometry(Math.abs(newStoich));

								Metabolite m = product.getMetabolite();
								//System.out.println(m.getName());
								product.setFormulaReactionComponent(m);
								Map<String, Integer> formulacambiada = product.getFormulaReactionComponent();
								for (Map.Entry<String, Integer> formul : formulacambiada.entrySet()) {
									System.out.println("FORMULA CAMBIADA");
									System.out.println(formul.getKey() + ":" + formul.getValue());
								}
								reactionsBalanced.add(this);
								result.put(true, "Adding atoms of one element in products");
								break outerLoop2;
							}
							else {
								result.put(false, "");
							}
						}

					}
					}
				}

			}


		}
		else if((reactants.size()+ products.size()) <= sumlistElemReactants.size()) {

			System.out.println(getId());

			double[][] ecuaciones = linarSystem();
			for (double[] filaEcuacion : ecuaciones) {
	            for (double valor : filaEcuacion) {
	                System.out.print(valor + " ");
	            }
	            System.out.println();
	        }

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
				result.put(false, "Undefined solution by system of equations");
			}
			else {
				result.put(true, "Solution by system of equations");
				int multiplicador = changeDoubleIntResult(solution);
				double[] resultFinal = new double[solution.length];
				for (int i = 0; i < solution.length; i++) {
					resultFinal[i] = solution[i] * multiplicador;
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
			result.put(true, "Impossible to solve by a system of equations");
		}
		else {
			result.put(false, "");
		}


		if (result.isEmpty()) {
			result.put(false, "");
        }

		return result;


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


}
