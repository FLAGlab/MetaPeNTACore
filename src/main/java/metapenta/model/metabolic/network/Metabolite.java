package metapenta.model.metabolic.network;

/**
 * Represents a metabolite that participates in chemical reactions
 * @author Jorge Duitama
 */
public class Metabolite {

	private int nid;
	private String id;
	private String name;
	private String compartment;
	private ChemicalFormula chemicalFormula;

	public Metabolite(String id, String name, String compartment, int nid) {
		super();
		this.id = id;
		this.name = name;
		this.nid = nid;
		this.compartment = compartment;
	}
	/**
	 * @return String chemical formula of this metabolite 
	 */
	public ChemicalFormula getChemicalFormula() {
		return chemicalFormula;
	}

	public void setChemicalFormula(String chemicalFormula) {
		ChemicalFormula formula = new ChemicalFormula(chemicalFormula);
		this.chemicalFormula = formula;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCompartment() {
		return compartment;
	}

	public int getNid() {
		return nid;
	}

	@Override
	public String toString() {		
		String out="{"+"\"id\": "+"\""+id+"\", \"name\":"+"\""+name+"\"}";
		return out;
	}
	
}
