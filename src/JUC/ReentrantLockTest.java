package JUC;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 判断公平锁和非公平锁的依据是模拟了thread2和thread3会竞争锁资源，然后以sleep时间代表是thread2还是thread3
 * thread2是sleep 250，thread3是sleep 200
 * 公平锁的情况下，250一定在200前面；非公平锁的情况下会出现200在250的前面
 * 非公平锁出现200在250之前的情况是因为thread3此时和AQS中thread2竞争，先拿到了锁，先输出了200
 * 公平锁的情况下thread3不会和AQS中的thread2竞争，加在了thread2的后面，所以结果稳定是200在250的后面
 * 使用sleep时间来表示thread2和thread3而不使用线程名的原因是：笔者使用的是线程池，线程池中的线程执行顺序和变量thread1、thread2、thread3没有关系，可能会出现线程3执行thread3，详细情况可以将第36行注释删除打印输出来看（可能需要多run几次或者修改59行的i的上限）
 * 第63行和第65行可以用于选择公平锁和非公平锁
 * @author lvgang
 * @date 2021/4/15 15:51
 */
public class ReentrantLockTest {

    static class TestThread implements Runnable {

        ReentrantLock lock;
        int sleepTime;
        CountDownLatch latch3Thread;

        TestThread(ReentrantLock lock, int sleepTime, CountDownLatch latch3Thread) {
            this.lock = lock;
            this.sleepTime = sleepTime;
            this.latch3Thread = latch3Thread;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                // System.out.println(Thread.currentThread().getName());
                System.out.println("sleep时间：" + sleepTime);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                latch3Thread.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 6, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {

            private final AtomicInteger atomicInteger = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "faceSchool线程_" + atomicInteger.getAndIncrement());
            }
        });

        for (int i = 0; i < 20; i++) {
            // latch3Thread的作用是让3个线程运行完之后再开始下一次公平锁或者非公平锁的结果的模拟，避免多次run，使得结果的对比不明显
            CountDownLatch latch3Thread = new CountDownLatch(3);
            // 非公平锁
             ReentrantLock lock = new ReentrantLock();
            // 公平锁
            // ReentrantLock lock = new ReentrantLock(true);

            TestThread thread1 = new TestThread(lock, 300,  latch3Thread);
            threadPoolExecutor.execute(thread1);
            // 此处的sleep是保证thread1获取了锁，避免和thread2竞争
            Thread.sleep(100);

            TestThread thread2 = new TestThread(lock, 250, latch3Thread);
            threadPoolExecutor.execute(thread2);
            // 此处的sleep是保证thread2进入了lock的AQS队列，顺便等待thread1运行完，模拟出thread2和thread3在非公平锁下的竞争情况
            Thread.sleep(200);

            TestThread thread3 = new TestThread(lock, 200, latch3Thread);
            threadPoolExecutor.execute(thread3);

            latch3Thread.await();
            System.out.println("--------------------------------------------------------");
        }

        threadPoolExecutor.shutdown();
    }
}
