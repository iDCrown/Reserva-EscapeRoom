package io.bootify.reserva.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AmenityDTO {

    private Long idAmenity;

    private String imageUrl;

    @Size(max = 255)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private Integer capacidad;

    @Size(max = 255)
    private String categoria;

    private Long reserva;

}
