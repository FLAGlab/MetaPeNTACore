package metapenta.model.metabolic.network;

public class Compartment {
	private String id;
	private String name;
	public Compartment(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	
}
