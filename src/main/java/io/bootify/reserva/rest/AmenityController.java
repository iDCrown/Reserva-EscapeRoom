package io.bootify.reserva.rest;

import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.service.AmenityService;
import io.bootify.reserva.service.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;
    private final JwtService jwtService;

    // Ruta para ver detalle de una sala y mostrar viewAmenity.html
    @GetMapping("/amenities/view/{idAmenity}")
    public String viewAmenity(Model model,
                              @PathVariable("idAmenity") final Long idAmenity,
                              @CookieValue(value = "token", required = false) String token) {

        // Si tienes token en cookie y quieres mostrar username en la vista
        if (token != null) {
            String username = jwtService.getUsernameFromToken(token);
            model.addAttribute("user", username);
        }

        // amenityService.get(...) debería devolver AmenityDTO (tu servicio ya tiene get)
        AmenityDTO amenityDto = amenityService.get(idAmenity);
        model.addAttribute("amenity", amenityDto);

        // La vista viewAmenity.html espera un objeto reserva en el formulario (th:object="${reserva}")
        model.addAttribute("reserva", new ReservaDTO());

        return "viewAmenity";
    }
}
