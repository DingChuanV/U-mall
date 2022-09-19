package com.uin.esclient.async;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 100, 5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        // 1.创建异步任务的对象--没有返回值的异步编排任务
//        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
//            log.info("当前线程的id:{}", Thread.currentThread().getId());
//            int result = 1 + 1;
//            log.info("结果:{}", result);
//        }, poolExecutor);

        // 2.创建异步任务的对象--有返回值的异步任务
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
                    return 10 / 2;
                }, poolExecutor)
                // 虽然可以感知异常，但是不能修改默认值
                .whenComplete((result, ex) -> {
                    log.info("结果:{}", result, "异常是:{}", ex);
                });
        // 可以感知异常，并且修改返回值
        //.exceptionally(t -> {
        //return 10;
        //});
        CompletableFuture<Integer> completableFutureHandler = CompletableFuture.supplyAsync(() -> {
                    return 10 / 2;
                }, poolExecutor)
                // 可以感知结果和异常，并能对结果修改
                .handle((res, ex) -> {
                    if (res != null) {
                        return 1;
                    }
                    if (ex != null) {
                        return res * 3;
                    }
                    return 0;
                });
        //不能感知上一步的执行结果
        CompletableFuture<Void> thenRunAsync = CompletableFuture.supplyAsync(() -> {
                    int result = 10 / 2;
                    return result;
                }, poolExecutor)
                .thenAcceptAsync((result) -> {
                    if (result == null) {
                        return;
                    }
                }, poolExecutor);

        // 3.两个任务组合 都完成
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("1当前线程" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("1运行结果" + i);
            return i;
        }, poolExecutor);

        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("2当前线程" + Thread.currentThread().getId());
            System.out.println("2结束");
            return "hello";
        }, poolExecutor);
        // 组合连个`future`，不需要获取`future`的结果，只需两个`future`处理完任务后，处理该任务
        future1.runAfterBothAsync(future2, () -> {
            System.out.println("3当前线程");
        }, poolExecutor);
        // 组合两个`future`，获取两个`future`任务的返回结果，然后处理任务，没有返回值
        future1.thenAcceptBothAsync(future2, (f1, f2) -> {
            System.out.println("任务3开始执行的结果" + f1 + f2);
        });
        // 组合两个`future`，获取两个`future`的返回结果，并返回当前任务的返回值
        future1.thenCombineAsync(future2, (f1, f2) -> {
            return f1 + " " + f2;
        }, poolExecutor);
        // 4.两个任务，只要有一个完成，我们就会执行任务3

        // 两个任务有一个执行完成，不需要获取`future`的结果，处理任务，也没有返回值。
        future1.runAfterEitherAsync(future2, () -> {
            System.out.println("任务3开始。。。之前的结果");
        }, poolExecutor);

        // 两个任务中有一个执行任务，获取它的返回值，处理任务，没有新的返回值
        future1.acceptEitherAsync(future2, (res) -> {
            System.out.println("任务3开始。。。之前的结果");
        });
        // 5.多任务组合
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的标题");
            return "date.json";
        }, poolExecutor);

        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片");
            return "date.json";
        }, poolExecutor);

        CompletableFuture<String> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "date.json";
        }, poolExecutor);
        // 等待所有任务完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(completableFuture1, completableFuture2,
                completableFuture3);
        // 只要有一个任务完成
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(completableFuture1, completableFuture2,
                completableFuture3);

        log.info("返回结果:{}", completableFuture.get());
        try {
            log.info("返回结果:{}", completableFuture.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
