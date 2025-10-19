package io.bootify.reserva.model;

import java.io.ObjectInputFilter.Status;

import io.bootify.reserva.domain.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long idUser;

    @Size(max = 255)
    private String nombre;

    @Size(max = 255)
    private String apellido;

    @Size(max = 255)
    private String tipoDocumento;

    @Size(max = 255)
    private String numeroDocumento;

    @Size(max = 255)
    private String telefono;

    @NotNull
    private Role role;

    private Status status;

}
