package metapenta.petrinet;

import java.util.ArrayList;
import java.util.List;

public class Place<O> {
    public static final String SINK = "SINK";
    public static final String SOURCE = "SOURCE";

    public static final String DOWN_CRITERIA = "DOWN";

    public static final String UP_CRITERIA = "UP";

    private String Id;
    private String label;
    private O object;
    List<Edge<Transition>> edgesIn;
    List<Edge<Transition>> edgesOut;

    public Place(String id, String label, O object) {
        Id = id;
        this.label = label;
        this.object = object;
        this.edgesIn = new ArrayList<>();
        this.edgesOut = new ArrayList<>();
    }

    public Place(Place<O> place) {
        Id = place.getID();
        this.label = place.getLabel();
        this.object = place.getObject();
        this.edgesIn = place.getEdgesIn();
        this.edgesOut = place.getEdgesOut();
    }
    public void setLabel(String label) {
    	
        this.label = label;
    }
    public void setId(String ID) {
        this.Id = ID;
    }
    public void setObject(O object){
        this.object = object;
    }

    public O getObject() {
       return object;
    }
    public String getLabel() {
        return label;
    }

    public List<Edge<Transition>> getEdgesIn() {
        return edgesIn;
    }

    public List<Edge<Transition>> getEdgesOut() {
        return edgesOut;
    }

    public String getID() {
        return Id;
    }
    public O getAttributes() {
        return object;
    }
    public void AddEdgeIn(Edge transition){
        this.edgesIn.add(transition);
    }
    public void AddEdgeOut(Edge transition){
        this.edgesOut.add(transition);
    }

    public boolean isStatus(String status){
    	if (SINK.equals(status)) return isSource();
    	if (SOURCE.equals(status)) return isSink();
    	return false;
        
    }

    public boolean isSource(){
        return edgesIn.isEmpty();
    }

    public boolean isSink(){
        return edgesOut.isEmpty();
    }

    public void addEdgeIn(Edge<Transition> edge) {
        edgesIn.add(edge);
    }

    public void addEdgeOut(Edge<Transition> edge) {
        edgesOut.add(edge);
    }


    public List<Transition> getTransitionsByCriteria(String criteria) {
        List<Transition> transitions = new ArrayList<>();

        for (Edge<Transition> edge: getEdgesByCriteria(criteria)) {
            transitions.add(edge.getTarget());
        }

        return transitions;
    }

    private List<Edge<Transition>> getEdgesByCriteria(String criteria) {
        switch (criteria) {
            case DOWN_CRITERIA:
                return edgesOut;
            case UP_CRITERIA:
                return edgesIn;
        }
        return null;
    }

    public List<Place<O>> getNeighbourDownEdgesgetNeighbourDownEdges() {
        List<Place<O>> neighbourEdges = new ArrayList<>();

        for (Edge<Transition> edge: getEdgesByCriteria(DOWN_CRITERIA)) {
            Transition transition = edge.getTarget();

            List<Edge<Place>> edgesOut = transition.getEdgesOut();
            for (Edge<Place> edgePlace : edgesOut){
                neighbourEdges.add(edgePlace.getTarget());
            }
        }


        return neighbourEdges;
    }


}
