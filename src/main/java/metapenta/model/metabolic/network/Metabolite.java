package metapenta.model.metabolic.network;

import java.util.List;

public class Metabolite implements ID {

	private int nid;
	private String id;
	private String name;
	private String sboTerm;
	private String compartmentId;
	private ChemicalFormula chemicalFormula;
	private boolean hasOnlySubstanceUnits = false;
	private boolean boundaryCondition = false;
	private int charge = 0;
	private List<String> links;

	public Metabolite(String id) {
		this.id = id;
	}

	public Metabolite(String id, String name, ChemicalFormula chemicalFormula, String compartmentId) {
		this.id = id;
		this.name = name;
		this.chemicalFormula = chemicalFormula;
		this.compartmentId = compartmentId;
	}

	public Metabolite(String id, String name, String compartmentId, int nid) {
		super();
		this.id = id;
		this.name = name;
		this.nid = nid;
		this.compartmentId = compartmentId;
	}

	public void setName(String name) {
		this.name = name;
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

	public String ID() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getSboTerm() {
		return sboTerm;
	}

	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
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
	
	
	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	@Override
	public String toString() {		
		String out="{"+"\"id\": "+"\""+id+"\", \"name\":"+"\""+name+"\"}";
		return out;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
