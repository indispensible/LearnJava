package JUC;

import java.util.concurrent.*;

/**
 * 使用信号量实现多线程交替输出结果
 * 将semaphoreSecond和semaphoreThird有关的内容注释掉就可以看到没有信号量的结果 second third first，增加了信号量的结果是first second third
 * @author lvgang
 * @date 2021/4/6 12:58
 */
public class SemaphoreTest {

    /**
     * semaphoreSecond和semaphoreThird的permits大于0的时候线程可以运行，否者线程将会阻塞，默认使用的是非公平锁
     * permits参数赋值给内部类Sync的sync对象的state属性
     * acquire()将sync对象的state属性加1
     * release()将sync对象的state属性减1
     * acquire()和release()使用的CAS乐观锁对state修改
     * */
    private Semaphore semaphoreSecond = new Semaphore(0);
    private Semaphore semaphoreThird = new Semaphore(0);

    public void printFirst() throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("first");
        semaphoreSecond.release();
    }

    public void printSecond() throws InterruptedException {
        semaphoreSecond.acquire();
        Thread.sleep(500);
        System.out.println("second");
        semaphoreThird.release();
    }

    public void printThird() throws InterruptedException {
        semaphoreThird.acquire();
        Thread.sleep(600);
        System.out.println("third");
    }

    public static void main(String[] args) {
        SemaphoreTest semaphoreTest = new SemaphoreTest();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        });
        threadPoolExecutor.execute(() -> {
            try {
                semaphoreTest.printFirst();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPoolExecutor.execute(() -> {
            try {
                semaphoreTest.printSecond();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPoolExecutor.execute(() -> {
            try {
                semaphoreTest.printThird();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadPoolExecutor.shutdown();
    }
}
