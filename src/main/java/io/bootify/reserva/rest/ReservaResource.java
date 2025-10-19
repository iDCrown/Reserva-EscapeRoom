package io.bootify.reserva.rest;

import io.bootify.reserva.domain.User;
import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.service.ReservaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.net.Authenticator;
import java.sql.Time;
import java.time.LocalTime;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.bootify.reserva.repos.UserRepository;



@Controller
@RequestMapping("/api/reservas")
public class ReservaResource {

    private final ReservaService reservaService;
    private final UserRepository userRepository;

    public ReservaResource(final ReservaService reservaService, final UserRepository userRepository) {
        this.reservaService = reservaService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> getAllReservas() {
        return ResponseEntity.ok(reservaService.findAll());
    }

    //Este mapeo es para mostrar el formulario de reserva pero realmente deberia ser el endpoind de detalle de un amenity
    @GetMapping("/verReserva")
    public String reserva(Model model) {
        model.addAttribute("reserva", new ReservaDTO());
        return "viewAmenity";
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDTO> getReserva(
            @PathVariable(name = "idReserva") final Long idReserva) {
        return ResponseEntity.ok(reservaService.get(idReserva));
    }

    @PostMapping("/createReserva")
    public String createReserva(@ModelAttribute("reserva") @Valid final ReservaDTO reservaDT, BindingResult result) {

        //Obtener el Id del usuario autenticado

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        reservaDTO.setUser(user.getIdUser());

        LocalTime horaInicio = reservaDTO.getHoraInicio();
        System.out.println("Hora de inicio recibida: " + horaInicio);


        if(horaInicio != null) {
            final LocalTime horaFin = horaInicio.plusHours(1);
            reservaDTO.setHoraFin(horaFin);
        }


        //validaciones
        if (reserva.getFechaReserva().isBefore(hoy)) {
        result.rejectValue("fechaReserva", "error.fechaReserva",
                "No puedes reservar fechas pasadas.");
            }

            if (reserva.getFechaReserva().isAfter(max)) {
                result.rejectValue("fechaReserva", "error.fechaReserva",
                        "La fecha de reserva no puede ser mayor a 2 meses desde hoy.");
            }

            // Verificar capacidad
            int capacidad = reservaService.obtenerCapacidadAmenity(reserva.getIdAmenity());
            if (reserva.getNumeroPersonas() > capacidad) {
                result.rejectValue("numeroPersonas", "error.numeroPersonas",
                        "El número de participantes excede la capacidad máxima (" + capacidad + ").");
            }

            if (result.hasErrors()) {
                return "reserva-form";
            }

        reservaService.create(reservaDTO);
        return "redirect:/homePage";
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<Long> updateReserva(
            @PathVariable(name = "idReserva") final Long idReserva,
            @RequestBody @Valid final ReservaDTO reservaDTO) {
        reservaService.update(idReserva, reservaDTO);
        return ResponseEntity.ok(idReserva);
    }

    @DeleteMapping("/{idReserva}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteReserva(
            @PathVariable(name = "idReserva") final Long idReserva) {
        reservaService.delete(idReserva);
        return ResponseEntity.noContent().build();
    }

}
