package io.bootify.reserva.model;

import io.bootify.reserva.domain.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;


@Getter
@Setter
public class ReservaDTO {

    private Long idReserva;

    @NotNull(message = "La fecha de reserva es obligatoria.")
    private LocalDate fechaReserva;

    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime horaInicio;

    private LocalTime horaFin;

    @Min(value = 3, message = "Debe haber al menos 3 participante.")
    private Integer numeroPersonas;

    private Long user;

    private Long amenity;

     //campo adicional para poder mostrar el nombre del amenity(sala) en la vista de mis reservas
    private String amenityNombre;
    //campo adicional para poder mostrar el estado de la reserva en la vista de mis reservas
    private StatusReserva statusReserva;


}
