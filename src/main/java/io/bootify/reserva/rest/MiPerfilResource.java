package io.bootify.reserva.rest;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import io.bootify.reserva.domain.User;
import io.bootify.reserva.repos.UserRepository;
import io.bootify.reserva.service.JwtService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MiPerfilResource {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    @GetMapping("/miPerfil")
    public String myAccountPage(Model model, @CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return "redirect:/auth/login";
        }

        try {
            String username = jwtService.getUsernameFromToken(token);
            User userEntity = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Solo nombre para el header
            model.addAttribute("user", userEntity.getUsername());
            // Objeto completo para myAccount
            model.addAttribute("userProfile", userEntity);
            
        } catch (Exception e) {
            return "redirect:/auth/login";
        }
        
        return "myAccount";
    }
}
