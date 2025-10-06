package io.bootify.reserva.model;

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

    @Size(max = 255)
    private String username;

    @Size(max = 255)
    private String password;

}
