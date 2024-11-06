package metapenta.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ReactionGroup {
	private String id;
	private String name;
	private String kind;
	private String sboTerm;
	private Map<String,Reaction> reactions = new TreeMap<>();
	public ReactionGroup (String id) {
		this.id=id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getSboTerm() {
		return sboTerm;
	}
	public void setSboTerm(String sboTerm) {
		this.sboTerm = sboTerm;
	}
	public List<Reaction> getReactions() {
		return new ArrayList<>(reactions.values());
	}
	public Set<String> getReactionIds() {
		return reactions.keySet();
	}
	public void setReactions(List<Reaction> reactions) {
		for(Reaction r:reactions) addReaction(r);
	}
	public void addReaction(Reaction r) {
		reactions.put(r.getId(),r);
	}
	public String getId() {
		return id;
	}
	public void removeReaction(String id) {
		reactions.remove(id);
		
	}
}
