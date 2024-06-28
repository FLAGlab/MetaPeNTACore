package metapenta.model.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Transition<A> {

    public static final String DOWN_CRITERIA = "DOWN";

    public static final String UP_CRITERIA = "UP";
    private int nId;
    private String ID;
    private String label;
    private A object;
    List<Edge<Place<?>>> edgesIn;
    List<Edge<Place<?>>> edgesOut;

    public Transition(int nId, String id, String label, A object) {
        this.nId = nId;
    	ID = id;
        this.label = label;
        this.object = object;
        this.edgesIn = new ArrayList<>();
        this.edgesOut = new ArrayList<>();
    }

    public int getnId() {
		return nId;
	}
    
    public String getID() {
        return ID;
    }

    public String getLabel() {
        return label;
    }

    public A getObject() {
        return object;
    }

    public void setObject(A attributes){
        this.object = attributes;
    }

    public void setID(String ID){
        this.ID = ID;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public void addEdgeIn(Edge<Place<?>> edge){
        this.edgesIn.add(edge);
    }

    public void addEdgesIn(List<Edge<Place<?>>> edges){
        for(Edge<Place<?>> edge: edges){
            this.edgesIn.add(edge);
        }
    }
    public void addEdgeOut(Edge<Place<?>> edge){
        this.edgesOut.add(edge);
    }

    public void addEdgesOut(List<Edge<Place<?>>> edgesOut){
        for(Edge<Place<?>> edge: edgesOut){
            this.edgesOut.add(edge);
        }
    }

    public List<Edge<Place<?>>> getEdgesOut() {
        return edgesOut;
    }

    public List<Edge<Place<?>>> getEdgesIn() {
        return edgesIn;
    }

    public List<Edge<Place<?>>> getAllEdges() {
        List<Edge<Place<?>>> allEdges = new ArrayList<>(edgesIn);
        allEdges.addAll(edgesOut);

        return allEdges;
    }

    public List<Place<?>> getPlacesByCriteria(String criteria) {
        List<Place<?>> places = new ArrayList<>();

        for (Edge<Place<?>> edge: getEdgesByCriteria(criteria)) {
            places.add(edge.getTarget());
        }
        return places;
    }

    private List<Edge<Place<?>>> getEdgesByCriteria(String criteria) {
        switch (criteria) {
            case DOWN_CRITERIA:
                return edgesOut;
            case UP_CRITERIA:
                return edgesIn;
        }
        return null;
    }

}
