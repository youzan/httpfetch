package com.github.youzan.httpfetch;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by daiqiang on 17/6/14.
 */
public class CommonUtilsTest {
    @Test
    public void formatObjectToJSONString() throws Exception {

        String stringArg = "123";
        Object rst = CommonUtils.formatObjectToJSONString(stringArg);
        assertEquals(stringArg, rst);

        int intArg = 123;
        rst = CommonUtils.formatObjectToJSONString(intArg);
        assertEquals(String.valueOf(intArg), rst);

        float floatArg = 123.1f;
        rst = CommonUtils.formatObjectToJSONString(floatArg);
        assertEquals(String.valueOf(floatArg), rst);

        Object nullArg = null;
        rst = CommonUtils.formatObjectToJSONString(nullArg);
        assertNull(rst);

        List listArg = new ArrayList();
        listArg.add("123");
        rst = CommonUtils.formatObjectToJSONString(listArg);
        assertEquals("[\"123\"]", rst);


    }

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