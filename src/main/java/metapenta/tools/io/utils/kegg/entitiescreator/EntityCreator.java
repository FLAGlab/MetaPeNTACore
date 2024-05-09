package metapenta.tools.io.utils.kegg.entitiescreator;

public interface EntityCreator<T> {
    T create(String id);
}
