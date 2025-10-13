package io.bootify.reserva.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityProvider {

    private final UserDetailsService userDetailsService;
    
    public SecurityProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder());
        
        return provider;
    }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/amenities/**", "/homePage", "/login", "/vistaSala", "/users/**", "/api/reservas/**").permitAll()
            .anyRequest().authenticated()
            )

            // .formLogin(form -> form
            //     .loginPage("/login")
            //     .defaultSuccessUrl("/homePage", true)
            //     .permitAll()
            // )
            // .logout(logout -> logout
            //     .logoutUrl("/logout")
            //     .logoutSuccessUrl("/homePage")
            //     .permitAll()
            // )
            .build();
    }
}
