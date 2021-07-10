package com.wzp.service;

import com.spring.*;

@Component("orderService")
@Scope("prototype")
public class OrderService implements BeanNameAware, InitializingBean {

    @Autowired
    private UserService userService;

    private String beanName;

    @Override
    public void afterPropertiesSet() {
        System.out.println("初始化");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println(name);
        this.beanName = name;
    }

    public UserService getUserService() {
        return userService;
    }
}
