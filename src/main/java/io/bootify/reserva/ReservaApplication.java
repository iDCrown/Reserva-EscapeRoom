package io.bootify.reserva;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Clase principal de la aplicación Spring Boot, la anotación @EnableScheduling permite la ejecución de tareas programadas, la cual esta siendo usada para revisar constantenemte los estados de las reservas.
@SpringBootApplication
@EnableScheduling
public class ReservaApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ReservaApplication.class, args);
    } 

}
