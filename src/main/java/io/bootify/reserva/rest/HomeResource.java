package io.bootify.reserva.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;


@Controller
public class HomeResource {

    @GetMapping("/homePage")
    public String index() {
        return "homePage";
    }

}
