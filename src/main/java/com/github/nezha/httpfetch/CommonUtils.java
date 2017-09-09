package com.github.nezha.httpfetch;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

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

    public static <T> T[] concatArray(T[]... arrays){
        if(isArrayEmpty(arrays)){
            return null;
        }
        int len = 0;
        for(T[] array : arrays){
            if(array != null){
                len += array.length;
            }
        }

        T[] newArray = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), len);
        int idx = 0;
        for(T[] array : arrays){
            if(array != null){
                System.arraycopy(array, 0, newArray, idx, array.length);
                idx += array.length;
            }
        }
        return newArray;
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

    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
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


    public static void main(String[] args) {
        List<String> aa = Arrays.asList("HDJZ1","HDJZ2","YRJZ1","HDJZ3","HDJZ4","HDJZ5","HDJZ6","HDJZ7","HDJZ8","HDJZ10","HDJZ11","HDJZ12","HDJZ13","HDJZ14","HDJZ15","HDJZ16","HDJZ17","HDJZ18","HDJZ19","HDJZ9","HDJZ20","HDJZ21","HDJZ22","HDJZ23","HDJZ24","HDJZ25","HDJZ26","HDJZ27","HDJZ28","HDJZ29","HDJZ30","HDJZ31","HDJZ32","HDJZ33","HDJZ34","HDJZ35","HDJZ36","HDJZ37","HDJZ38","HDJZ39","HDJZ40","HDJZ42","HDJZ43","HDJZ44","HDJZ45","HDJZ46","YRJZ2","YRJZ3","YRJZ4","YRJZ6","YRJZ7","YRJZ9","YRJZ10","YRJZ11","YRJZ12","YRJZ14","YRJZ15","YRJZ16","YRJZ18","YRJZ19","YRJZ20","HDJZ47","HDJZ48","HDJZ49","HDJZ50","HDXD51","HDXD52","HDXD53","HDXD54","HDXD55","HDXD56","HDXD57","HDXD58","HDXD59","HDXD60","HDXD61","HDXD62","HDXD63","HDXD64","HDXD65","HDXD66","HDXD67","HDXD68","HDXD69","HDXD70","HDXD71","HDXD72","HDXD73","HDXD74","HDXD75","HDXD76","HDXD77","HDXD78","HDXD79","HDXD80","HDXD81","HDXD82","HDXD83","HDXD84","HDXD85","HDXD86","HDXD87","HDXD88","HDXD89","HDXD90","HDXD91","HDXD92","HDXD93","HDXD94","HDXD96","HDXD97","HDXD98","HDXD99","HDXD100","YRJZ5","YRJZ8","YRJZ13","YRJZ17","HDJC101","HDJC102","HDJC103","HDJC104","HDJC106","HDJC107","HDJC108","HDJC109","HDJC110","HDJC112","HDJC113","HDJC114","HDJC115","HDJC116","HDJC117","HDJC118","HDJC119","HDJC120","HDJC121","HDJC122","HDJC123","HDJC124","HDJC125","HDJC126","HDJC127","HDJC128","HDJC129","HDJC131","HDJC134","HDJC135","HDJC136","HDJC137","HDJC138","HDJC139","HDJC140","HDJC141","HDJC142","HDJC143","HDJC144","HDJC145","HDJC146","HDJC147","HDJC148","HDJC149","HDJC150","HDXF151","HDXF152","HDXF153","HDXF154","HDXF155","HDXF156","HDXF157","HDXF158","HDXF159","HDXF160","HDXF161","HDXF162","HDXF163","HDXF164","HDXF165","HDXF166","HDXF167","HDXF168","HDXF169","HDXF170","HDXF171","HDXF172","HDXF173","HDXF174","HDXF175","HDXF176","HDXF177","HDXF178","HDXF179","HDXF180","HDXF181","HDXF182","HDXF183","HDXF184","HDXF185","HDXF186","HDXF187","HDXF188","HDXF189","HDXF190","HDXF191","HDXF192","HDXF193","HDXF194","HDXF195","HDXF196","HDXF197","HDXF198","HDXF199","YRXD21","YRXD23","YRXD24","YRXD26","YRXD27","YRXD28","YRXD29","YRXD30","YRXD31","YRXD32","YRXD33","YRXD34","YRXD35","YRXD36","YRXD37","YRXD38","YRXD39","YRXD40","YRJC41","YRJC42","YRJC43","YRJC44","YRJC45","YRJC46","YRJC47","YRJC48","YRJC49","YRJC50","YRJC51","YRJC52","YRJC53","YRJC54","YRJC55","YRJC56","YRJC57","YRJC58","YRJC59","YRJC60","YRXF61","YRXF62","YRXF64","YRXF65","YRXF66","YRXF67","YRXF68","YRXF70","YRXF71","YRXF72","YRXF73","YRXF74","YRXF75","YRXF76","YRXF77","YRXF78","YRXF79","YRXF80","YRXF80-副本","YRXF78-副本","YRXF76-副本","YRXF74-副本","YRXF72-副本","YRXF71-副本","YRXF69-副本","YRXF68-副本","YRXF63-副本","YRJC52-副本","YRJC53-副本","YRJC55-副本","YRJC57-副本","YRJC59-副本","HDJZ41","HDXD-95","HDJC105","HDJC130","HDJC132","YRXD22","YRXD25","YRXF63","YRXF69","HDJC133","HDJC111","HDXF200","HDXF201","HDXF202","HDXF203","HDXF204","HDXF205","HDXF206","HDXF207","HDXF208","HDXF209","HDXF210","HDXF211","HDXF212","HDXF213","HDXF214","HDXF215","HDXF216","HDXF217","HDJC201","HDJC202","HDJC203","HDJC204","HDJC205","HDJC206","HDJC207","HDJC208","HDJC209","HDJC210","HDJC211","HDJC212","HDJC213","HDJC214","HDJC215","HDJC216","HDJC216-副本","HDJC217","HDXD201","HDXD202","HDXD203","HDXD204","HDXD205","HDXD206","HDXD207","HDXD208","HDXD209","HDXD209-副本","HDXD210","HDXD211","HDXD212","HDXD213","HDXD214","HDXD215","HDXD216","HDJZ201","HDJZ202","HDJZ203","HDJZ204","HDJZ205","HDJZ206","HDJZ207","HDJZ208","HDJZ209","HDJZ210","HDJZ212","HDJZ211","HDJZ213","HDJZ214","HDJZ215","HDJZ216","HDJZ217");

        List<String> limit = new ArrayList<>();

        for(int i=201; i< 217+1;i++){
            limit.add("HDXF"+i);
            limit.add("HDJC"+i);
            limit.add("HDXD"+i);
            limit.add("HDJZ"+i);
        }

        Iterator<String> iterator = limit.iterator();
        while (iterator.hasNext()){
            String name = iterator.next();
            for (String a : aa){
                if(name.equals(a)){
                    iterator.remove();
                    continue;
                }
            }
        }
        System.out.println(limit);

    }
}
