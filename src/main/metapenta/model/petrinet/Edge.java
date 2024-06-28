package metapenta.model.petrinet;

public class Edge<T> {
    private T target;
    private double weight;
    public Edge(T target, double weight) {
        this.target = target;
        this.weight = weight;
    }
    public T getTarget() {
        return target;
    }
    public double getWeight() {
        return weight;
    }
    public void setTarget(T target){
         this.target = target;
    }
    public void setWeight(int weight){
        this.weight = weight;
    }

}
