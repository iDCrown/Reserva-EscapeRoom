package io.bootify.reserva.model;

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

}
