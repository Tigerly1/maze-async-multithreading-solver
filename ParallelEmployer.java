import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelEmployer implements Employer {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<Location, AtomicInteger> orderIds = new ConcurrentHashMap<>();
    private final Map<Integer, CompletableFuture<Result>> resultFutures = new ConcurrentHashMap<>();
    private final AtomicBoolean exitFound = new AtomicBoolean(false);
    private OrderInterface orderInterface;
    private volatile Location exitLocation;

    @Override
    public void setOrderInterface(OrderInterface order) {
        this.orderInterface = order;
        this.orderInterface.setResultListener(result -> {
            CompletableFuture<Result> resultFuture = resultFutures.get(result.orderID());
            if (resultFuture != null) {
                resultFuture.complete(result);
            }
        });
    }
    @Override
    public Location findExit(Location startLocation, List<Direction> allowedDirections) {
        explore(startLocation);
        awaitCompletion();
        shutdownAndAwaitTermination(executor);
        return exitLocation;
    }

    private void explore(Location location) {
        if (exitFound.get() || !isNewLocation(location)) return;

        CompletableFuture<Result> future = new CompletableFuture<>();
        int orderId = orderInterface.order(location);
        resultFutures.put(orderId, future);

        future.thenAccept(result -> {
            try {
                if(exitFound.get()) return;
                processResult(location, result);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                // Handle exception - log error or exit loop
            }
        }).exceptionally(e -> {
            executor.submit(()-> explore(location));
            // Handle exception - log error or take appropriate action
            return null;
        });
    }

    private void processResult(Location location, Result result) {
        if (result.type() == LocationType.EXIT) {
            exitFound.set(true);
            exitLocation = location;
            resultFutures.values().forEach(future -> future.cancel(true));
            shutdownAndTerminateExecutor();
        } else if (result.type() == LocationType.PASSAGE) {
            result.allowedDirections().forEach(direction -> {
                Location newLocation = direction.step(location);
                executor.submit(() -> explore(newLocation));
            });
        }
    }

    private boolean isNewLocation(Location location) {
        return orderIds.computeIfAbsent(location, k -> new AtomicInteger(0)).getAndIncrement() == 0;
    }

    private void awaitCompletion() {
        try {
            while (!exitFound.get()) {
                Thread.sleep(100); // Sleep a bit to reduce CPU usage
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for completion.");
        } finally {
            shutdownAndTerminateExecutor();
        }
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown();  // Interrupt all running tasks immediately

        try {
            if (!pool.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                pool.shutdown();  // Try to cancel tasks if not terminated
                if (!pool.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                    System.err.println("Executor did not terminate.");
                }
            }
        } catch (InterruptedException ie) {
            pool.shutdownNow();  // Cancel if interrupted during waiting
            Thread.currentThread().interrupt();
        }
    }

    private void shutdownAndTerminateExecutor() {
        executor.shutdownNow(); // Attempt to stop all actively executing tasks
        for (CompletableFuture<Result> future : resultFutures.values()) {
            future.cancel(true); // Anuluje oczekujące zadania
        }


    }
}
