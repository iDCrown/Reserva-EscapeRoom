package io.bootify.reserva.repos;

import io.bootify.reserva.domain.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Amenity findFirstByReservaIdReserva(Long idReserva);

}
