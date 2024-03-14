package metapenta.model.metabolic.network;

/**
 * Represents a gene product (usually an enzyme) that participates in one reaction
 * @author Jorge Duitama
 */
public class GeneProduct implements Comparable<GeneProduct>{
	private String id;
	private String name;
	private String label;
	private String sboTerm;
	
	/**
	 * Builds a new gene product
	 * @param id of the product
	 * @param name of the product
	 */
	public GeneProduct(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	/**
	 * @return String product id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return String product name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return String product label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * Changes the label
	 * @param label new product label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	public String getSboTerm() {
		return sboTerm;
	}
	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
	}
	@Override
	public int compareTo(GeneProduct o) {	
		return this.id.compareTo(o.id);
	}
	@Override
	public String toString() {
		return "GeneProduct [id=" + id + ", name=" + name + "]";
	}
	
	
	
}
