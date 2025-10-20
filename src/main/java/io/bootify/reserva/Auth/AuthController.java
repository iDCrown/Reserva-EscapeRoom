package io.bootify.reserva.Auth;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.bootify.reserva.model.UserRegisterDTO;
import io.bootify.reserva.service.AuthService;
import io.bootify.reserva.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping(value = "login")
    public  String login(@ModelAttribute LoginRequest request, Model model, HttpServletResponse responses) {
        try {
        AuthResponse response = authService.login(request);

        // Cookie JWT
        Cookie cookie = new Cookie("token", response.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        responses.addCookie(cookie);

        return "redirect:/homePage";
    } catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        return "/auth/login";
    }
    }

    @GetMapping(value = "login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping(value = "register")
    public String register (Model model) {
        model.addAttribute("user", new UserRegisterDTO());
        return "register";

    }

    @PostMapping(value = "register")
    public String createUser(
        @Valid @ModelAttribute("user") UserRegisterDTO user,BindingResult result,RedirectAttributes redirectAttributes,Model model) {

        // Validaciones manuales adicionales
        if (userService.existsByCorreo(user.getCorreo())) {
            result.rejectValue("correo", "error.user", "Este correo ya está registrado");
        }

        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Este nombre de usuario ya está en uso");
        }

        if (user.getNombre().equalsIgnoreCase(user.getApellido())) {
            result.rejectValue("apellido", "error.user", "El apellido no puede ser igual al nombre");
        }

        // Si hay errores, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        // Guardar usuario
        userService.create(user);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado exitosamente.");

        return "redirect:/auth/login";
    }
}
