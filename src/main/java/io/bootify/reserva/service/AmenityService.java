/* package io.bootify.reserva.service;

import io.bootify.reserva.domain.Amenity;
import io.bootify.reserva.events.BeforeDeleteReserva;
import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.repos.AmenityRepository;
import io.bootify.reserva.util.NotFoundException;
import io.bootify.reserva.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AmenityService {

    private final AmenityRepository amenityRepository;


    public AmenityService(final AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
      
    }

    public List<AmenityDTO> findAll() {
        return amenityRepository.findAll()
                .stream()
                .map(amenity -> mapToDTO(amenity, new AmenityDTO()))
                .toList();
    }

    public AmenityDTO get(final Long idAmenity) {
        return amenityRepository.findById(idAmenity)
                .map(amenity -> mapToDTO(amenity, new AmenityDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AmenityDTO amenityDTO) {
        final Amenity amenity = new Amenity();
        mapToEntity(amenityDTO, amenity);
        return amenityRepository.save(amenity).getIdAmenity();
    }

    public void update(final Long idAmenity, final AmenityDTO amenityDTO) {
        final Amenity amenity = amenityRepository.findById(idAmenity)
                .orElseThrow(NotFoundException::new);
        mapToEntity(amenityDTO, amenity);
        amenityRepository.save(amenity);
    }

    public void delete(final Long idAmenity) {
        final Amenity amenity = amenityRepository.findById(idAmenity)
                .orElseThrow(NotFoundException::new);
        amenityRepository.delete(amenity);
    }

    private AmenityDTO mapToDTO(final Amenity amenity, final AmenityDTO amenityDTO) {
        amenityDTO.setIdAmenity(amenity.getIdAmenity());
        amenityDTO.setImageUrl(amenity.getImageUrl());
        amenityDTO.setNombre(amenity.getNombre());
        amenityDTO.setDescripcion(amenity.getDescripcion());
        amenityDTO.setCapacidad(amenity.getCapacidad());
        amenityDTO.setCategoria(amenity.getCategoria());
        return amenityDTO;
    }

    private Amenity mapToEntity(final AmenityDTO amenityDTO, final Amenity amenity) {
        amenity.setNombre(amenityDTO.getNombre());
        amenity.setImageUrl(amenityDTO.getImageUrl());
        amenity.setDescripcion(amenityDTO.getDescripcion());
        amenity.setCapacidad(amenityDTO.getCapacidad());
        amenity.setCategoria(amenityDTO.getCategoria());
        return amenity;
    }

}
 */