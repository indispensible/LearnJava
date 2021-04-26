package Common;

import com.alibaba.fastjson.JSONArray;

/**
 * 查看类是哪个classLoader加载的
 * @author lvgang
 * @date 2021/4/25 14:59
 */
public class ClassLoaderTest {

    public static void classLoaderPrint(Class testClass) {
        ClassLoader classLoader = testClass.getClassLoader();
        if (classLoader == null) {
            System.out.println("BootStrap加载");
        } else {
            System.out.println(classLoader.toString());
        }
    }

    public static void main(String[] args) {
        ClassLoaderTest.classLoaderPrint(ClassLoaderTest.class);
        ClassLoaderTest.classLoaderPrint(String.class);
        ClassLoaderTest.classLoaderPrint(JSONArray.class);
    }
}
