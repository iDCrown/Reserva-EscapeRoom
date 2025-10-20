package io.bootify.reserva.rest;

import io.bootify.reserva.domain.User;
import io.bootify.reserva.model.ChangePasswordRequest;
import io.bootify.reserva.model.UserDTO;
import io.bootify.reserva.model.UserRegisterDTO;
import io.bootify.reserva.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "idUser") final Long idUser) {
        return ResponseEntity.ok(userService.get(idUser));
    }



    @PutMapping("/{idUser}")
    public ResponseEntity<Long> updateUser(@PathVariable(name = "idUser") final Long idUser,
        @Valid @RequestBody final UserDTO userDTO,
        BindingResult result) {

            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(null);
            }

        userService.updateUser(idUser, userDTO);
        return ResponseEntity.ok(idUser);
    }

    @DeleteMapping("/{idUser}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "idUser") final Long idUser) {
        userService.delete(idUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/login")
    public String welcome(){
        return "Welcome to Spring Security";
    }

    /*
     * ====================================================
     * - Endpoints dedicados al usuario no administrador - (/me)
     * ====================================================
     */

     //Con este método se obtiene la información del usuario que ha iniciado sesión
     @GetMapping("/me")
     public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
         return ResponseEntity.ok(userService.getCurrentUser(userDetails));
     }

     //Con este método se actualiza la información del usuario que ha iniciado sesión
     @PutMapping("/me")
     public ResponseEntity<Long> updateCurrentUser(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody UserDTO userDTO,
        BindingResult result) {
            if (result.hasErrors()) {
                // Devuelve los errores al frontend
                return ResponseEntity.badRequest().body(null);
            }

            UserDTO currentUser = userService.getCurrentUser(userDetails);
            if (userDTO.getRole() == null) {
                userDTO.setRole(currentUser.getRole());
            }

    // Establece el rol actual si no se envía desde el frontend
    if (userDTO.getRole() == null) {
        userDTO.setRole(currentUser.getRole());
    }

    userService.updateUser(currentUser.getIdUser(), userDTO);
    return ResponseEntity.ok(currentUser.getIdUser());
}

     @PatchMapping("/me/deactivate")
     public ResponseEntity<Void> deactivateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
        HttpServletResponse response) {
         UserDTO currentUser = userService.getCurrentUser(userDetails);
         userService.deactivateUser(currentUser.getIdUser());

         // 🔥 Invalida el token y la sesión (desde el backend)
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0); // expira inmediatamente
        response.addCookie(tokenCookie);

        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
     }

     @PatchMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody Map<String, String> body) {

        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        String confirmPassword = body.get("confirmPassword");

        userService.changePassword(userDetails, currentPassword, newPassword, confirmPassword);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleGenericException(Exception ex) {
        // Esto es un "catch-all" por si algo no previsto falla.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocurrió un error inesperado"));
    }

}
