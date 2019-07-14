package cd.test;

import cd.model.Bean;
import cd.model.TestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author James Chen
 * @date 2019/7/6.
 */
public class TestReflect {

//    private static final Logger LOGGER = LoggerFactory.getLogger(TestReflect.class);

    private static final java.security.Permission ACCESS_PERMISSION =
            new ReflectPermission("suppressAccessChecks");

    public static void testReflectClass() throws Exception {
        //  constructors
        Constructor[] constructors = TestBean.class.getConstructors();
        // find default constructor
        for(Constructor constructor : constructors){
            if(constructor.getParameters().length == 0){
                System.out.println("TestBean default constructor" + constructor.getDeclaringClass());
            }
        }
        // methods
        Method[] methods = TestBean.class.getMethods();
        for(Method method : methods){
            System.out.println("method name "+method.getName());
            System.out.println("method param "+Arrays.asList(method.getParameters()));
        }
        // fields
        Class curClass = TestBean.class;
        while(curClass != Object.class) {
            Field[] fields = curClass.getDeclaredFields();
            for (Field field : fields) {
                System.out.println("field name " + field.getName());
                System.out.println("field type " + field.getType());
                field.setAccessible(true);
            }
            curClass = curClass.getSuperclass();
        }
        // annotations
        Annotation[] annotations = TestBean.class.getAnnotations();
        for(Annotation annotation : annotations){
            System.out.println("annotation name "+annotation.toString());
            System.out.println("annotation type "+annotation.annotationType());
        }
    }

    public static boolean checkAccessAble(){
        SecurityManager securityManager = System.getSecurityManager();
        boolean checked = false;
        try {
            if (securityManager != null) {
                securityManager.checkPermission(ACCESS_PERMISSION);
            }
            checked = true;
        }catch (SecurityException e){
            e.printStackTrace();
        }
        return checked;
    }

    public static <T> T testInvoke(Class<T> clazz, Map<String, Object> map) throws Exception{
        T object =  clazz.newInstance();
        Method[] methods = clazz.getMethods();
        for(Method method : methods){
            String methodName = method.getName();
            if(! methodName.startsWith("set")) {
                continue;
            }
            String fieldName = method.getName().substring("set".length()).toLowerCase();
            if(map.containsKey(fieldName)) {
                Parameter parameter = method.getParameters()[0];
                Object value = parameter.getType().cast(map.get(fieldName));
                method.invoke(object, value);
            }
        }
        return object;
    }

    public static <T> T testField(Class<T> clazz, Map<String, Object> map) throws Exception {
       T object = clazz.newInstance();
       if(checkAccessAble()) {
           Class<?> curClazz = clazz;
           while (curClazz != Object.class) {
               Field[] fields = curClazz.getDeclaredFields();
               for (Field field : fields) {
                   if (map.containsKey(field.getName())) {
                       // Class cd.test.TestReflect can not access a member of class cd.model.TestBean
                       // with modifiers "private"
                       field.setAccessible(true);
                       field.set(object, map.get(field.getName()));
                   }
               }
               curClazz = curClazz.getSuperclass();
           }
       }
       return object;
    }

    public static void testInvokeGet() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object object = TestBean.class.newInstance();
        Method[] methods = TestBean.class.getMethods();
        for(Method method : methods){
            if (method.getName().startsWith("set")){
                method.invoke(object, "1111");
            }
        }
        for(Method method : methods){
            if (method.getName().startsWith("get")){
                Object result = method.invoke(object, null);
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id","123456");
        map.put("name","cd");
//        TestBean testBean = testInvoke(TestBean.class, map);
//        TestBean testBean = testField(TestBean.class, map);
//        System.out.println(testBean.getId() + "__" + testBean.getName());
//        testInvokeGet();
        ClassLoader classLoader = TestReflect.class.getClassLoader();
        ClassLoader extClassLoader = classLoader.getParent();
        ClassLoader bootStrapClassLoader = extClassLoader.getParent();
        System.out.println(classLoader);
        System.out.println(extClassLoader);
        System.out.println(bootStrapClassLoader);
    }
}
