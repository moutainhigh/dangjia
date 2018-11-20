package com.dangjia.acg.common.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 有关Class处理的工具类
 *
 * @author QiYuXiang
 */
public final class ClazzUtil {

    private ClazzUtil() {

    }

    /***
     * 反射调用静态方法
     *
     * @param clazz       类
     * @param methodName  方法
     * @param paramTypes  类型
     * @param paramValues 值
     * @return
     * @throws Exception 异常
     */
    public static Object invokeStatic(Class<?> clazz, String methodName,
                                      Class<?>[] paramTypes, Object[] paramValues) throws Exception {
        Method method = clazz.getMethod(methodName, paramTypes);
        return method.invoke(clazz, paramValues);
    }

    /***
     * 获得定义Class时声明的父类的泛型参数的类型.默认Object.class.例如:UserDao extends</br>
     * HibernateDao<User,Long>,则getSuperClassGenricType(UserDao,0)返回User.class
     *
     * @param clazz 类
     * @param index 坐标
     * @return
     */
    public static Class<?> getSuperClassGenricType(final Class<?> clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }

        if (!(params[index] instanceof Class<?>)) {
            return Object.class;
        }

        return (Class<?>) params[index];
    }

    /**
     * 获得定义Class时声明的父类的泛型参数的第一个类型.
     *
     * @param clazz 类
     */
    public static Class<?> getSuperClassGenricType(final Class<?> clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 判断类继承
     *
     * @param clazz    类
     * @param destFace 是否继承的父类
     * @return true/false
     */
    public static boolean isExtends(Class<?> clazz, Class<?> destFace) {
        if (clazz.equals(destFace)) {
            return true;
        }
        if (null != clazz.getSuperclass()) {
            return isExtends(clazz.getSuperclass(), destFace);
        }
        return false;
    }

    /**
     * 判断是否实现了某接口.
     *
     * @param clazz    类
     * @param destFace 接口
     * @return false/true
     */
    public static boolean isImplement(Class<?> clazz, Class<?> destFace) {

        if (clazz.equals(destFace)) {
            return true;
        }

        Class<?>[] faces = clazz.getInterfaces();

        for (Class<?> face : faces) {
            if (isImplement(face, destFace)) {
                return true;
            }
        }

        if (null != clazz.getSuperclass()) {
            return isImplement(clazz.getSuperclass(), destFace);
        }
        return false;
    }

    /**
     * 获取Bean的成员变量,去除static,final属性
     *
     * @param clazz 类
     */
    public static List<Field> getBeanFileds(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            list.add(field);
        }
        return list;
    }

    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static void invokeSetter(Object obj, String propertyName, Object value) {
        Object object = obj;
        String[] names = StringUtils.split(propertyName, ".");
        for (int i=0; i<names.length; i++){
            if(i<names.length-1){
                String getterMethodName = "get" + StringUtils.capitalize(names[i]);
                object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
            }else{
                String setterMethodName = "set" + StringUtils.capitalize(names[i]);
                invokeMethodByName(object, setterMethodName, new Object[] { value });
            }
        }
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
     * 同时匹配方法名+参数类型，
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
                                      final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }


        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

        /**
         * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
         * 如向上转型到Object仍无法找到, 返回null.
         * 匹配函数名+参数类型。
         *
         * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
         */
        public static Method getAccessibleMethod(final Object obj, final String methodName,final Class<?>... parameterTypes) {


            for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
                try {
                    Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
                    makeAccessible(method);
                    return method;
                } catch (NoSuchMethodException e) {
                    // Method不在当前类定义,继续向上转型
                    continue;// new add
                }
            }
            return null;
        }

        /**
         * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
         */
        public static void makeAccessible(Method method) {
            if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                    && !method.isAccessible()) {
                method.setAccessible(true);
            }
        }

        /**
         * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
         */
        public static void makeAccessible(Field field) {
            if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
                    .isFinal(field.getModifiers())) && !field.isAccessible()) {
                field.setAccessible(true);
            }
        }

    /**
     * 直接调用对象方法, 无视private/protected修饰符，
     * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
     * 只匹配函数名，如果有多个同名函数调用第一个。
     */
    public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
        Method method = getAccessibleMethodByName(obj, methodName);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }


        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    public static Object invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        for (String name : StringUtils.split(propertyName, ".")){
            String getterMethodName = "get" + StringUtils.capitalize(name);
            object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
        }
        return object;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 只匹配函数名。
     *
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     */
    public static Method getAccessibleMethodByName(final Object obj, final String methodName) {


        for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    makeAccessible(method);
                    return method;
                }
            }
        }
        return null;
    }
    /**
     * @Author QiYuXiang
     * @Description 从一个包中查找出所有类,在jar包中不能查找
     * @Param
     * @Return
     * @Date 2018/3/23 上午10:47
     */
    public static List<Class> getClassesByPackageName(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * @Author QiYuXiang
     * @Description 递归查找文件夹下面的所有文件
     * @Param
     * @Return
     * @Date 2018/3/23 上午10:47
     */
    public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //递归查找文件夹下面的所有文件
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + '.' + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}
