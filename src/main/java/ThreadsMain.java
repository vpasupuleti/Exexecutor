import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Contains implementation of Runnable & Callable tasks
 * Use Executor service to run the tasks created.
 * Uses Executor service to run multiple tasks to run parallel.
 * Uses Scheduled Executor service to run callable task.
 */
public class ThreadsMain {

    public static void main(String []args){
        implementRunnableThread();
        implementRunnableExecutor();
        implementCallableExecutor();
        implementParellelCallableExecutor();
        implementCallableScheduledExecutor();
    }

    private static void implementRunnableThread(){
        System.out.println("implementRunnableThread - Begin");
        Runnable task = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println("Foo "+threadName);
                TimeUnit.SECONDS.sleep(3);
                System.out.println("Bar "+threadName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        //task.run();
        Thread thread = new Thread(task);
        thread.start();
        System.out.println("Done!");
        System.out.println("implementRunnableThread - End");
    }

    private static void implementRunnableExecutor(){
        System.out.println("implementRunnableExecutor - Begin");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello: "+threadName);
        });
        try {
            if (!executorService.isShutdown()){
                executorService.shutdown();
                executorService.awaitTermination(4, TimeUnit.SECONDS);
            }

        }catch (InterruptedException e){
            System.out.println("Task interrupted");
        }
        finally{
            if (!executorService.isTerminated()){
                System.out.println("Cancle non-finished tasks");
            }
            executorService.shutdownNow();
            System.out.println("Shutdown finished!");
        }
        System.out.println("implementRunnableExecutor - End");
    }

    private static void implementCallableExecutor(){
        System.out.println("implementCallableExecutor - Begin");
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Integer> future = executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                return 123;
            }
            catch (InterruptedException e) {
                throw new IllegalStateException("task interrupted", e);
            }
        });

        try {
            Integer result = future.get();
            System.out.println("Future: "+result);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            if (!executorService.isShutdown() || !executorService.isTerminated()){
                executorService.shutdown();
                System.out.println("Executor shutdown!");
            }
        }
        System.out.println("implementCallableExecutor - End");
    }

    private static void implementParellelCallableExecutor(){
        Callable<String> callableTask1 = () -> {
            TimeUnit.SECONDS.sleep(4);
          System.out.println("Callable Task1 Executed after 4 seconds");
          return "Task1";
        };
        Callable<String> callableTask2 = () -> {
            TimeUnit.SECONDS.sleep(3);
            System.out.println("Callable Task2 Executed after 3 seconds");
            return "Task2";
        };
        Callable<String> callableTask3 = () -> {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("Callable Task3 Executed after 5 seconds");
            return "Task3";
        };
        Callable<String> callableTask4 = () -> {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("Callable Task4 Executed after 2 seconds");
            return "Task4";
        };

        List<Callable<String>> callableList = Arrays.asList(callableTask1,callableTask2,callableTask3,callableTask4);
        ExecutorService executorService = Executors.newWorkStealingPool();
        try {
            executorService.invokeAll(callableList).stream().map(ThreadsMain::apply).forEach(System.out::println);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void implementCallableScheduledExecutor(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> {
            System.out.println("Callable task in scheduled executor");
            return "future";
        }, 3, TimeUnit.SECONDS);
        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("Remaining delay: %sms", remainingDelay);
    }

    private static String apply(Future<String> x) {
        try {
            return x.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        return null;
    }
}
