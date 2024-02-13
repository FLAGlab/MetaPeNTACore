package metapenta.model.errors;

public class GeneProductDoesNotExitsException extends Exception {
    public GeneProductDoesNotExitsException(String id){
        super(id);
    }
}
