package com.toyspring.application.controller;

import com.toyspring.application.service.ApplicationService;
import com.toyspring.core.Model;
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

    @CommandMapping("/modelTest")
    public void modelTest(Model model, String id) {
        model.addAttribute("test", "잘되나");
        System.out.println("model : " + model.getAttribute("test") + ",id : " + id);
    }

    @CommandMapping("/exit")
    public void exit() {
        System.out.println("프로그램 종료");
        System.exit(0);
    }
}
