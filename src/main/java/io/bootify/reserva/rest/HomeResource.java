package io.bootify.reserva.rest;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import io.bootify.reserva.model.AmenityDTO;
/* import io.bootify.reserva.service.AmenityService; */

@Controller
public class HomeResource {

    /* private final AmenityService amenityService;

    public HomeResource(AmenityService amenityService) {
        this.amenityService = amenityService;
    } */

    @GetMapping("/homePage")
    public String index(Model model) {
        /* model.addAttribute("amenity", amenityService.findAll()); */
        return "homePage";
    }

}
