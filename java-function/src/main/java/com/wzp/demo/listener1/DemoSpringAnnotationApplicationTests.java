package com.wzp.demo.listener1;

import com.wzp.demo.listener2.ListenerEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoSpringAnnotationApplicationTests {

    private IWorkListener listener;

    public void setWorkListener(IWorkListener IWorkListener) {
        this.listener = IWorkListener;
    }

    public void work(ListenerEvent event) {
        listener.onStart(event);
    }

    @Test
    public void test1() {
        setWorkListener(new IWorkListener() {
            @Override
            public void onStart(ListenerEvent name) {
                System.out.println("Start work for " + name.toString() + " !");
            }
        });
//        setWorkListener(name -> System.out.println("Start work for " + name + " !"));
        work(new ListenerEvent(1, "boss1"));
        work(new ListenerEvent(1, "boss2"));
        work(new ListenerEvent(1, "boss3"));
    }

    @Test
    public void test2() {
        // 继承实现类设置监听器
        setWorkListener(new MyIWorkListener());
        // 工作
        work(new ListenerEvent(1, "boss11"));
        work(new ListenerEvent(1, "boss22"));
        work(new ListenerEvent(1, "boss33"));
    }

    class MyIWorkListener implements IWorkListener {

        @Override
        public void onStart(ListenerEvent name) {
            System.out.println("Start work for " + name.toString() + " !");
        }

    }
}