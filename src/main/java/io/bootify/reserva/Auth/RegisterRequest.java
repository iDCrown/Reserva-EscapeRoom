package io.bootify.reserva.Auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    String Nombre;
    String Apellido;
    String TipoDocumento;
    String NumeroDocumento;
    String Telefono;
    String username;
    String password;
}
