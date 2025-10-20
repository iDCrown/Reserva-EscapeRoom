package io.bootify.reserva.model;

import java.io.ObjectInputFilter.Status;

import io.bootify.reserva.domain.Role;
import io.bootify.reserva.valid.ValidEmailDomain;
import io.bootify.reserva.valid.ValidName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long idUser;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^(?!.*(.)\\1{2,})[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El nombre solo puede contener letras y espacios, sin repeticiones excesivas."
    )
    @ValidName
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, max = 30, message = "El apellido debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^(?!.*(.)\\1{2,})[A-Za-zÁÉÍÓÚáéíóúñÑ ]+$", message = "El apellido solo puede contener letras y espacios, sin repeticiones excesivas."
    )
    @ValidName
    private String apellido;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "\\d{6,20}", message = "El número de documento debe tener entre 6 y 20 dígitos")
    private String numeroDocumento;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe tener entre 7 y 15 dígitos válidos")
    @Size(max = 10)
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @ValidEmailDomain
    @Size(max = 255)
    private String correo;

    @NotNull
    private Role role;

    private Status status;

}
