package io.bootify.reserva.service;

import io.bootify.reserva.domain.Amenity;
import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.events.BeforeDeleteReserva;
import io.bootify.reserva.model.AmenityDTO;
import io.bootify.reserva.repos.AmenityRepository;
import io.bootify.reserva.repos.ReservaRepository;
import io.bootify.reserva.util.NotFoundException;
import io.bootify.reserva.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class AmenityService {

    private final AmenityRepository amenityRepository;
    private final ReservaRepository reservaRepository;

    public AmenityService(final AmenityRepository amenityRepository,
            final ReservaRepository reservaRepository) {
        this.amenityRepository = amenityRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<AmenityDTO> findAll() {
        final List<Amenity> amenities = amenityRepository.findAll(Sort.by("idAmenity"));
        return amenities.stream()
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
        amenityDTO.setNombre(amenity.getNombre());
        amenityDTO.setDescripcion(amenity.getDescripcion());
        amenityDTO.setCapacidad(amenity.getCapacidad());
        amenityDTO.setCategoria(amenity.getCategoria());
        amenityDTO.setReserva(amenity.getReserva() == null ? null : amenity.getReserva().getIdReserva());
        return amenityDTO;
    }

    private Amenity mapToEntity(final AmenityDTO amenityDTO, final Amenity amenity) {
        amenity.setNombre(amenityDTO.getNombre());
        amenity.setDescripcion(amenityDTO.getDescripcion());
        amenity.setCapacidad(amenityDTO.getCapacidad());
        amenity.setCategoria(amenityDTO.getCategoria());
        final Reserva reserva = amenityDTO.getReserva() == null ? null : reservaRepository.findById(amenityDTO.getReserva())
                .orElseThrow(() -> new NotFoundException("reserva not found"));
        amenity.setReserva(reserva);
        return amenity;
    }

    @EventListener(BeforeDeleteReserva.class)
    public void on(final BeforeDeleteReserva event) {
        final ReferencedException referencedException = new ReferencedException();
        final Amenity reservaAmenity = amenityRepository.findFirstByReservaIdReserva(event.getIdReserva());
        if (reservaAmenity != null) {
            referencedException.setKey("reserva.amenity.reserva.referenced");
            referencedException.addParam(reservaAmenity.getIdAmenity());
            throw referencedException;
        }
    }

}
