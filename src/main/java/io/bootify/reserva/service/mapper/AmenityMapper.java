/* package io.bootify.reserva.service.mapper;
import io.bootify.reserva.domain.Amenity;
import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.model.AmenityDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityDTO toDto(Amenity entity);
    Amenity toEntity(AmenityDTO amenityDTO);

    default Reserva map(Long id) {
        if(id == null){
            return null;
        }
         Reserva reserva = new Reserva();
         reserva.setIdReserva(id);
         return reserva;
    }

    default Long map(Reserva reserva) {
       return reserva != null ? reserva.getIdReserva() : null;
    }
}
 */