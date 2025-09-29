package io.bootify.reserva.repos;

import io.bootify.reserva.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Reserva findFirstByUserIdUser(Long idUser);

}
