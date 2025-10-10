package io.bootify.reserva.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.bootify.reserva.model.UserRegisterDTO;
import io.bootify.reserva.service.AuthService;
import io.bootify.reserva.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping(value = "register")
    public String register (Model model) {
        model.addAttribute("user", new UserRegisterDTO());
        return "register";

    }

    @PostMapping(value = "register")
    public  String createUser(@ModelAttribute("user")  UserRegisterDTO UserRegisterDTO, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        userService.create(UserRegisterDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado exitosamente.");
        return "redirect:/homePage";
    }
}
