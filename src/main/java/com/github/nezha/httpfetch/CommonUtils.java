package com.github.nezha.httpfetch;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;

/**
 * Created by daiqiang on 17/3/14.
 */
public class CommonUtils {

    public static boolean isCollectionEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isInLimit(String beCompared, String... compareds) {
        if (ArrayUtils.isNotEmpty(compareds)) {
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

}
