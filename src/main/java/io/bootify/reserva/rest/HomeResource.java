package io.bootify.reserva.rest;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.service.AmenityService;
import io.bootify.reserva.domain.User;
import io.bootify.reserva.service.JwtService;

@Controller
public class HomeResource {

    private final AmenityService amenityService;
    private final JwtService jwtService;

    public HomeResource(AmenityService amenityService, JwtService jwtService) {
        this.amenityService = amenityService;
        this.jwtService = jwtService;
    }

    @GetMapping("/homePage")
    public String index(Model model, @CookieValue(value = "token", required = false) String token) {
        if (token != null) {
        // Decodificar el JWT (ejemplo usando tu servicio)
        String user = jwtService.getUsernameFromToken(token);
        model.addAttribute("user", user);
    }
        model.addAttribute("amenity", amenityService.findAll());
        return "homePage";
    }

}
