package DesignPattern;

/**
 * 单例模式双重校验锁实现和测试
 * @author lvgang
 * @date 2021/4/7 16:06
 */
public class Singleton {

    private volatile static Singleton instance;
    private volatile int version;

    public int getVersion() {
        return version;
    }

    private Singleton(int version) {
        this.version = version;
    }

    public static Singleton getInstance(int version) throws InterruptedException {
        if (instance == null) {
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
