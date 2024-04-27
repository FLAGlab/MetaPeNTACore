package metapenta.model.metabolic.network;

/**
 * Represents a metabolite that participates in chemical reactions
 * @author Jorge Duitama
 */
public class Metabolite {

	private int nid;
	private String id;
	private String name;
	private String compartmentId;
	private ChemicalFormula chemicalFormula;
	private boolean hasOnlySubstanceUnits = false;
	private boolean boundaryCondition = false;
	private int charge = 0;
	

	public Metabolite(String id, String name, String compartmentId, int nid) {
		super();
		this.id = id;
		this.name = name;
		this.nid = nid;
		this.compartmentId = compartmentId;
	}

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

	public String getCompartmentId() {
		return compartmentId;
	}
	public int getNid() {
		return nid;
	}
	
	public boolean isHasOnlySubstanceUnits() {
		return hasOnlySubstanceUnits;
	}
	public void setHasOnlySubstanceUnits(boolean hasOnlySubstanceUnits) {
		this.hasOnlySubstanceUnits = hasOnlySubstanceUnits;
	}
	public boolean isBoundaryCondition() {
		return boundaryCondition;
	}
	public void setBoundaryCondition(boolean boundaryCondition) {
		this.boundaryCondition = boundaryCondition;
	}
	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	@Override
	public String toString() {		
		String out="{"+"\"id\": "+"\""+id+"\", \"name\":"+"\""+name+"\"}";
		return out;
	}
	
}
