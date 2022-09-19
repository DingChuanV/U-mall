package com.uin.esclient.async;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class AsyncTest {
    public static ExecutorService service = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //1.Thread
        //Thread thread = new ThreadDemo();
        //thread.start();
        //2.Runnable
        //Runnable runnableDemo = new RunnableDemo();
        //new Thread(runnableDemo).start();
        //3.Callable+FutureTask（Future是一个接口，FutureTask是它的实现类）
        //FutureTask<Integer> futureTask = new FutureTask<>(new CallableDemo());
        //new Thread(futureTask).start();
        //（阻塞）等待整个线程执行完成，获取返回结果
        //Integer integer = futureTask.get();
        //System.out.println(integer);
        //4.线程池
        //当前系统中池只有一两个，每个异步任务，提交给线程池让他自己去执行
        //private static ExecutorService service = Executors.newFixedThreadPool(5);
        /**
         * execute()和submit()的区别
         * 1. execute()只是去去执行
         * 2. submit()会有返回值
         */
        Future<?> submit = service.submit(new RunnableDemo());
        /**
         * 4中创建线程池有什么区别？
         * 1. 1/2没有返回值 3可以有返回值
         * 2. 1/2/3 比较浪费资源 4可以控制，性能稳定
         */
        /**
         * Creates a new {@code ThreadPoolExecutor} with the given initial
         * parameters.
         *
         * @param corePoolSize the number of threads to keep in the pool, even
         *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
         * @param maximumPoolSize the maximum number of threads to allow in the
         *        pool
         * @param keepAliveTime when the number of threads is greater than
         *        the core, this is the maximum time that excess idle threads
         *        will wait for new tasks before terminating.
         * @param unit the time unit for the {@code keepAliveTime} argument
         * @param workQueue the queue to use for holding tasks before they are
         *        executed.  This queue will hold only the {@code Runnable}
         *        tasks submitted by the {@code execute} method.
         * @param threadFactory the factory to use when the executor
         *        creates a new thread
         * @param handler the handler to use when execution is blocked
         *        because the thread bounds and queue capacities are reached
         * @throws IllegalArgumentException if one of the following holds:<br>
         *         {@code corePoolSize < 0}<br>
         *         {@code keepAliveTime < 0}<br>
         *         {@code maximumPoolSize <= 0}<br>
         *         {@code maximumPoolSize < corePoolSize}
         * @throws NullPointerException if {@code workQueue}
         *         or {@code threadFactory} or {@code handler} is null
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
                200,
                2,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

    }

    public static class ThreadDemo extends Thread {
        @Override
        public void run() {
            log.info("当前线程:{}", Thread.currentThread().getId());
        }
    }

    public static class RunnableDemo implements Runnable {
        @Override
        public void run() {
            log.info("当前线程:{}", Thread.currentThread().getId());
        }
    }

    public static class CallableDemo implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            log.info("当前线程:{}", Thread.currentThread().getId());
            return 1 + 1;
        }
    }
}
