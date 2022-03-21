package com.toyspring.application.controller;

import com.toyspring.application.service.TestService;
import com.toyspring.core.annotation.Autowired;
import com.toyspring.core.annotation.Controller;

@Controller
public class TestController {

    @Autowired
    public TestService testService;

}
