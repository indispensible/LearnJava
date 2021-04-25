package DesignPattern;

/**
 * 单例模式双重校验锁实现和测试
 * 单例模式的使用场景：创建的对象耗时长、内存占用多，又经常需要使用的对象。例如bert模型或者知识图谱等，不用每次创建对象都重新加载一遍，可以用单例模式做懒汉或者饿汉加载，减少内存的占用
 * @author lvgang
 * @date 2021/4/7 16:06
 */
public class Singleton {

    /**
     * volatile修饰符是避免指令重排导致未被初始化的instance被引用而报错
     * 对象创建可以认为是三个步骤：
     * 1、分配内存
     * 2、在分别配的内存上初始化一个Singleton类的实例
     * 3、将声明的引用instance指向这块内存
     * 在上述的三个步骤中存在2和3进行调换，这就会导致线程A在第35行代码处给引用变量instance赋了值，不为null，但是此时instance指向的内存位置还没有初始化，此时线程B从第31行直接跳到39行，在后续的使用中就会报错了
     * */
    private volatile static Singleton instance;
    private volatile int version = -2;

    public int getVersion() {
        return version;
    }

    private Singleton(int version) {
        this.version = version;
    }

    public static Singleton getInstance(int version) throws InterruptedException {
        if (instance == null) {
            // synchronized下还有一个判断instance是否为null是因为会有多个线程进入到32行进行等待锁释放，要是没有34行的判断就会让instance被多次初始化
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton(version);
                }
            }
        }
        return instance;
    }


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            int version = i + 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("线程名：" + Thread.currentThread().getName() + "；实例版本号：" + Singleton.getInstance(version).getVersion());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

}
