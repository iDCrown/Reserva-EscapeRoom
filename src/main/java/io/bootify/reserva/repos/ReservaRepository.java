package io.bootify.reserva.repos;

import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.domain.StatusReserva;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Reserva findFirstByUserIdUser(Long idUser);

    //Nuevo metodo para obtener todas las reservas de un usuario
    List<Reserva> findAllByUserIdUser(Long idUser);

    //Nuevo metodo para obtener todas las reservas por estado
    List<Reserva> findAllByStatusReserva(StatusReserva statusReserva);

    List<Reserva> findAllByFechaReservaAndStatusReserva(LocalDate fecha, StatusReserva status);


}
