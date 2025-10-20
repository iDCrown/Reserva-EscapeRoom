package io.bootify.reserva.rest;

import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.domain.StatusReserva;
import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.service.AmenityService;
import io.bootify.reserva.service.JwtService;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.bootify.reserva.repos.ReservaRepository;;

@Controller
@AllArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;
    private final JwtService jwtService;
    private final ReservaRepository reservaRepository;

    @GetMapping("/reservas/ocupadas")
    @ResponseBody
    public List<String> obtenerHorasOcupadas(@RequestParam("fecha") LocalDate fecha) {
        List<Reserva> reservas = reservaRepository.findAllByFechaReservaAndStatusReserva(fecha, StatusReserva.RESERVADA);

        return reservas.stream()
                .map(reserva -> reserva.getHoraInicio().toString()) // ✅ Convertimos LocalTime → String
                .collect(Collectors.toList());
    }

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

        List<Reserva> HoursTrue =  reservaRepository.findAllByStatusReserva(StatusReserva.RESERVADA);


        // La vista viewAmenity.html espera un objeto reserva en el formulario (th:object="${reserva}")
        model.addAttribute("reserva", new ReservaDTO());

        return "viewAmenity";
    }
}
