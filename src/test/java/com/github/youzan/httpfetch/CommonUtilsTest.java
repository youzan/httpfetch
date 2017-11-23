package com.github.youzan.httpfetch;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by daiqiang on 17/6/14.
 */
public class CommonUtilsTest {

    @Test
    public void testConcatArray() throws Exception {
        Object[] object = CommonUtils.concatArray(null);
        assertNull(object);

        String[] array = CommonUtils.concatArray(new String[]{"1", "2"}, null, new String[]{}, new String[]{"3","4"});
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, array);

        String[] array2 = CommonUtils.concatArray(new String[]{"1", "2"}, new String[]{"3","4"});
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, array2);
    }
}