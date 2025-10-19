package io.bootify.reserva.model;

import io.bootify.reserva.domain.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReservaDTO {

    private Long idReserva;

    private LocalDate fechaReserva;

    @Schema(type = "string", example = "18:30")
    private LocalTime horaInicio;

    @Schema(type = "string", example = "18:30")
    private LocalTime horaFin;

    private Integer numeroPersonas;

    private Long user;

    private Long amenity;
    //campo adicional para poder mostrar el nombre del amenity(sala) en la vista de mis reservas
    private String amenityNombre;
    //campo adicional para poder mostrar el estado de la reserva en la vista de mis reservas
    private StatusReserva statusReserva;



}
