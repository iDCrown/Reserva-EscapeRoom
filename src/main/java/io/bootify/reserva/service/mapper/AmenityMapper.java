/* package io.bootify.reserva.service.mapper;
import io.bootify.reserva.domain.Amenity;
import io.bootify.reserva.model.AmenityDTO;
import org.mapstruct.Mapper;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityDTO toDto(Amenity entity);
    Amenity toEntity(AmenityDTO amenityDTO);

    // Custom mapping method for Set<Reserva> to Long
    default Long map(Set<io.bootify.reserva.domain.Reserva> reservaSet) {
        // Example: return the size of the set, or implement your own logic
        return reservaSet == null ? null : (long) reservaSet.size();
    }

    // Custom mapping method for Long to Set<Reserva>
    default Set<io.bootify.reserva.domain.Reserva> map(Long value) {
        // Implement your logic to convert Long to Set<Reserva>
        // For example, return an empty set or fetch from DB if needed
        return java.util.Collections.emptySet();
    }

}
 */