package metapenta.model.errors;

public class GeneProductDoesNotExitsException extends Exception {
    public GeneProductDoesNotExitsException(String id){
        super("Gene product with ID " + id + " does not exist in metabolic network");
    }
}
