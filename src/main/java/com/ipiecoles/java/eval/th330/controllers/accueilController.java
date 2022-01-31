package com.ipiecoles.java.eval.th330.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class accueilController {


    @GetMapping(value = "/")
    public String accueil(final ModelMap model) {

        return "accueil";
    }
}
