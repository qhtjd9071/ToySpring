package com.toyspring.application.contoller;

import com.toyspring.core.annotation.Autowired;
import com.toyspring.core.annotation.Controller;
import com.toyspring.application.service.TestService;

@Controller
public class TestController {

    @Autowired
    public TestService testService;

}
