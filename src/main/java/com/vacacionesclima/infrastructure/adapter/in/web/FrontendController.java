package com.vacacionesclima.infrastructure.adapter.in.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple entry-point adapter that forwards the root path to the static
 * frontend assets packaged inside src/main/resources/frontend.
 */
@Controller
public class FrontendController {

    @GetMapping({"/", ""})
    public String index() {
        return "forward:/index.html";
    }
}
