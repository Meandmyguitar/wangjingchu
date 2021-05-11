package com.wzp.demo.listener1;

import com.wzp.demo.listener2.ListenerEvent;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private IWorkListener listener;

    public void setWorkListener(IWorkListener IWorkListener) {
        this.listener = IWorkListener;
    }

    public void work(ListenerEvent event) {
        listener.onStart(event);
    }
}