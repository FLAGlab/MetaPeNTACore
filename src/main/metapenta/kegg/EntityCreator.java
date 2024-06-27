package metapenta.kegg;

public interface EntityCreator<T> {
    T create(String id);
}
