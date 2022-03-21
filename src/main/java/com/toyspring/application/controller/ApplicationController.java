package com.toyspring.application.controller;

import com.toyspring.application.service.ApplicationService;
import com.toyspring.core.annotation.Autowired;
import com.toyspring.core.annotation.CommandMapping;
import com.toyspring.core.annotation.Controller;

@Controller
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @CommandMapping("/printTest")
    public void printTest() {
        String ret = applicationService.printTest();
        System.out.println(ret);
    }

}
