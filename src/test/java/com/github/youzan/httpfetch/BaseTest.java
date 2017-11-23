package com.github.youzan.httpfetch;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by daiqiang on 17/6/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/application-httpapi.xml", "classpath:spring/application-service.xml"})
public abstract class BaseTest {
}