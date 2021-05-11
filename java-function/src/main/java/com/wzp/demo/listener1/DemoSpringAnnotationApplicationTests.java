package com.wzp.demo.listener1;

import com.wzp.demo.listener2.ListenerEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoSpringAnnotationApplicationTests {

    @Resource
    private MyService myService;

    @Test
    public void test1() {
        myService.setWorkListener(new IWorkListener() {
            @Override
            public void onStart(ListenerEvent name) {
                System.out.println("Start work for " + name.toString() + " !");
            }
        });
//        myService.setWorkListener(name -> System.out.println("Start work for " + name + " !"));
        myService.work(new ListenerEvent(1, "boss1"));
        myService.work(new ListenerEvent(1, "boss2"));
        myService.work(new ListenerEvent(1, "boss3"));
    }

    @Test
    public void test2() {
        // 继承实现类设置监听器
        myService.setWorkListener(new MyIWorkListener());
        // 工作
        myService.work(new ListenerEvent(1, "boss11"));
        myService.work(new ListenerEvent(1, "boss22"));
        myService.work(new ListenerEvent(1, "boss33"));
    }

    class MyIWorkListener implements IWorkListener {

        @Override
        public void onStart(ListenerEvent name) {
            System.out.println("Start work for " + name.toString() + " !");
        }

    }
}