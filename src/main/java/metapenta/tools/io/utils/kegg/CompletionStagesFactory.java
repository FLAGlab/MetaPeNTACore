package metapenta.tools.io.utils.kegg;

import metapenta.model.metabolic.network.ID;
import metapenta.tools.io.utils.kegg.entitiescreator.EntityCreator;

import java.util.*;
import java.util.concurrent.*;

public class CompletionStagesFactory<T extends ID> {
    private EntityCreator<T> creator;
    private Collection<String> ids;
    private ExecutorService executor;

    public CompletionStagesFactory(CompletionStateParams<T> completionStateParams) {
        this.creator = completionStateParams.getCreator();
        this.ids = completionStateParams.getIds();
        this.executor = Executors.newFixedThreadPool(1);
    }

    public Collection<T> createEntities() {
        Collection<T> result = createEntitiesCompletionStage().toCompletableFuture().join();
        executor.shutdown();

        return result;
    }

    private CompletionStage<Collection<T>> createEntitiesCompletionStage() {
        List<CompletableFuture<T>> futures = createFutures(ids);

        CompletableFuture<Collection<T>> future = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[ids.size()]))
                .thenApply(id -> {
                    Collection<T> allEntities = new HashSet<>();
                    for (CompletableFuture<T> f : futures) {
                        T entity = f.join();
                        allEntities.add(entity);
                    }
                    return allEntities;
                });

        return future;
    }

    private List<CompletableFuture<T>> createFutures(Collection<String> IDs) {
        List<CompletableFuture<T>> futures = new ArrayList<>();

        for (String id : IDs) {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> creator.create(id), executor);
            futures.add(future);
        }

        return futures;
    }

    public void setIds(Collection<String> ids) {
        this.ids = ids;
    }
}