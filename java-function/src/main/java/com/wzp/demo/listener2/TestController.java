package com.wzp.demo.listener2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class TestController {

    @Resource
    private WorkListenerParser workListenerParser;

    @GetMapping("/work")
    public Object work() {
        workListenerParser.work(new ListenerEvent(UUID.randomUUID().hashCode(), UUID.randomUUID().toString()));
        return "done";
    }

    @WorkListener
    public void listener(ListenerEvent name) {
        System.out.println("Start work for " + name.toString() + " !");
    }
}