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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping(value = "login")
    public /* ResponseEntity<String> */ String login(@ModelAttribute /* @RequestBody */ LoginRequest request, Model model, HttpServletResponse responses) {
        AuthResponse response = authService.login(request);
        /* return ResponseEntity.ok(response.getToken()); */

        if (response == null) {
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        return "/auth/login"; // vuelve al login si falla
    }

        //Creación de una cookie que mantenga la sesión iniciada con el token JWT
        Cookie cookie = new Cookie("token", response.getToken());
        cookie.setHttpOnly(true); // Se evita el acceso desde JavaScript
        cookie.setPath("/"); // La cookie es válida para todo el dominio
        cookie.setMaxAge(24 * 60 * 60); // La cookie dura un día

        responses.addCookie(cookie);

        return "redirect:/homePage";
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
    public  String createUser(@ModelAttribute("user")  UserRegisterDTO UserRegisterDTO, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        userService.create(UserRegisterDTO);
        redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado exitosamente.");
        return "redirect:/homePage";
    }
}
