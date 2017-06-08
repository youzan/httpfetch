package com.github.nezha.httpfetch;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by daiqiang on 17/3/14.
 */
public class CommonUtils {

    public static boolean isCollectionEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isArrayEmpty(Object[] array){
        return array == null || array.length == 0;
    }

    public static boolean isArrayEmpty(byte[] array){
        return array == null || array.length == 0;
    }

    public static boolean isStringEmpty(CharSequence str){
        return str == null || str.length() == 0;
    }

    public static boolean isInLimit(String beCompared, String... compareds) {
        if (!isArrayEmpty(compareds)) {
            for (String compared : compareds) {
                if (beCompared == compared || beCompared.equals(compared)) {
                    return true;
                }
            }
            return false;
        } else {
            String msg = String.format("没有待比较的字符串! beCompared [%s]", beCompared);
            throw new IllegalArgumentException(msg);
        }
    }


    /**
     * 获取所有的field 包括父类
     * @param clazz
     * @return
     */
    public static Set<Field> getAllFieldWithSuper(Class clazz){
        if(clazz == null){
            throw new RuntimeException("参数clazz为空!");
        }
        Set<Field> fields = new HashSet<>();
        do{
            Field[] fieldArray = clazz.getDeclaredFields();
            for(Field field : fieldArray){
                if(!fields.contains(field)){
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }while (!(clazz.equals(Object.class)));
        return fields;
    }

    /**
     * 校验是否简单类型
     *
     * @param clazz
     * @return
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("参数有误!");
        }
        if (clazz.isPrimitive()) {
            return true;
        }
        if (clazz == Boolean.class
                || clazz == Character.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Void.class
                ) {
            return true;
        }
        return false;
    }

}
