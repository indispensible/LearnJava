import java.lang.ref.WeakReference;

/**
 * @author lvgang
 * @date 2021/4/22 15:02
 */
public class ReferenceTest {
    public static void weakReferenceTest() {
        String str = "有强引用对象不会被清理";
        WeakReference<String> weak1 = new WeakReference<>(str);
        WeakReference<String> weak2 = new WeakReference<>(new String("没有强引用对象会被清理"));
        // 字符串常量池中存的是引用
        WeakReference<String> weak3 = new WeakReference<>("字符串常量池中常量引用是强引用不会被GC");
        System.gc();

        if (weak1.get() == null) {
            System.out.println("weak1的referent被清理了");
        } else {
            System.out.println(weak1.get());
        }

        if (weak2.get() == null) {
            System.out.println("weak2的referent被清理了");
        } else {
            System.out.println(weak2.get());
        }

        if (weak3.get() == null) {
            System.out.println("weak3的referent被清理了");
        } else {
            System.out.println(weak3.get());
        }
    }

    public static void main(String[] args) {
        ReferenceTest.weakReferenceTest();
    }
}
