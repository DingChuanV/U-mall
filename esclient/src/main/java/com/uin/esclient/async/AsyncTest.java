package com.uin.esclient.async;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.execute.Execute;

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
