package io.bootify.reserva.rest;

import io.bootify.reserva.domain.User;
import io.bootify.reserva.model.UserDTO;
import io.bootify.reserva.model.UserRegisterDTO;
import io.bootify.reserva.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import org.springframework.ui.Model;

import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/users")
public class UserResource {

    private final UserService userService;

    public UserResource(final UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "idUser") final Long idUser) {
        return ResponseEntity.ok(userService.get(idUser));
    }


    @GetMapping("/register")
    public String register (Model model) {
        model.addAttribute("user", new UserRegisterDTO());
        return "register";

    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("user") UserRegisterDTO UserRegisterDTO) {

        if(UserRegisterDTO.getPassword() == null || UserRegisterDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        UserRegisterDTO.setPassword(passwordEncoder.encode(UserRegisterDTO.getPassword()));
        userService.create(UserRegisterDTO);
        return "redirect:/homePage";
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<Long> updateUser(@PathVariable(name = "idUser") final Long idUser,
            @RequestBody @Valid final UserDTO userDTO) {
            
        userService.update(idUser, userDTO);
        return ResponseEntity.ok(idUser);
    }

    @DeleteMapping("/{idUser}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "idUser") final Long idUser) {
        userService.delete(idUser);
        return ResponseEntity.noContent().build();
    }

}
