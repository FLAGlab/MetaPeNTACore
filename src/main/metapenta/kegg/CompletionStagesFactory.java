package metapenta.kegg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CompletionStagesFactory<T> {
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
