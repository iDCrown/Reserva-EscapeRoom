package io.bootify.reserva.service;

import io.bootify.reserva.domain.Amenity;
import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.domain.StatusReserva;
import io.bootify.reserva.domain.User;
import io.bootify.reserva.events.BeforeDeleteReserva;
import io.bootify.reserva.events.BeforeDeleteUser;
import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.repos.ReservaRepository;
import io.bootify.reserva.repos.UserRepository;
import io.bootify.reserva.repos.AmenityRepository;
import io.bootify.reserva.util.NotFoundException;
import io.bootify.reserva.util.ReferencedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final ApplicationEventPublisher publisher;

    public ReservaService(final ReservaRepository reservaRepository,
            final UserRepository userRepository, final ApplicationEventPublisher publisher, final AmenityRepository amenityRepository) {
        this.reservaRepository = reservaRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.publisher = publisher;
    }

    public List<ReservaDTO> findAll() {
        final List<Reserva> reservas = reservaRepository.findAll(Sort.by("idReserva"));
        return reservas.stream()
                .map(reserva -> mapToDTO(reserva, new ReservaDTO()))
                .toList();
    }

    public ReservaDTO get(final Long idReserva) {
        return reservaRepository.findById(idReserva)
                .map(reserva -> mapToDTO(reserva, new ReservaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ReservaDTO reservaDTO) {
        final Reserva reserva = new Reserva();
        mapToEntity(reservaDTO, reserva);
        return reservaRepository.save(reserva).getIdReserva();
    }

    public void update(final Long idReserva, final ReservaDTO reservaDTO) {
        final Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(NotFoundException::new);
        mapToEntity(reservaDTO, reserva);
        reservaRepository.save(reserva);
    }

    public void delete(final Long idReserva) {
        final Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteReserva(idReserva));
        reservaRepository.delete(reserva);
    }

    private ReservaDTO mapToDTO(final Reserva reserva, final ReservaDTO reservaDTO) {
        reservaDTO.setIdReserva(reserva.getIdReserva());
        reservaDTO.setFechaReserva(reserva.getFechaReserva());
        reservaDTO.setHoraInicio(reserva.getHoraInicio());
        reservaDTO.setHoraFin(reserva.getHoraFin());
        reservaDTO.setNumeroPersonas(reserva.getNumeroPersonas());
        reservaDTO.setUser(reserva.getUser() == null ? null : reserva.getUser().getIdUser());
        reservaDTO.setAmenity(reserva.getAmenity() == null ? null : reserva.getAmenity().getIdAmenity());
        return reservaDTO;
    }

    private Reserva mapToEntity(final ReservaDTO reservaDTO, final Reserva reserva) {
        reserva.setFechaReserva(reservaDTO.getFechaReserva());
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFin(reservaDTO.getHoraFin());
        reserva.setNumeroPersonas(reservaDTO.getNumeroPersonas());
        final User user = reservaDTO.getUser() == null ? null : userRepository.findById(reservaDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
        reserva.setUser(user);
        final Amenity amenity = reservaDTO.getAmenity() == null ? null : amenityRepository.findById(reservaDTO.getAmenity())
                .orElseThrow(() -> new NotFoundException("amenity not found"));
        reserva.setAmenity(amenity);
        return reserva;
    }

    @EventListener(BeforeDeleteUser.class)
    public void on(final BeforeDeleteUser event) {
        final ReferencedException referencedException = new ReferencedException();
        final Reserva userReserva = reservaRepository.findFirstByUserIdUser(event.getIdUser());
        if (userReserva != null) {
            referencedException.setKey("user.reserva.user.referenced");
            referencedException.addParam(userReserva.getIdReserva());
            throw referencedException;
        }
    }

    /*
     * ===========================================
     * Métodos para mis reservas y lógica detras de los estados de reservas
     * ===========================================
     */

     //Método para obtener todas las reservas de un usuario por medio de su id y mapear el nombre del amenity

     @Transactional(readOnly = true)
    public List<ReservaDTO> findAllByUserId(final Long idUser) {
        // Se utiliza el método simple del repository
        final List<Reserva> reservas = reservaRepository.findAllByUserIdUser(idUser);
        return reservas.stream()
                .map(this::mapToDTOWithAmenityAndStatus)
                .collect(Collectors.toList());
    }


    // Método para chequear si una reserva debe ser marcada como consumida según las condiciones como fecha y hora. También se encarga de marcarla y guardarla.
    @Transactional
    protected void checkAndMarkConsumedIfNeeded(final Reserva reserva) {
        if (reserva == null) return;
        if (reserva.getStatusReserva() != StatusReserva.RESERVADA) return;

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LocalDate fecha = reserva.getFechaReserva();
        LocalTime horaInicio = reserva.getHoraInicio();

        boolean shouldConsume = false;

        if (fecha == null || horaInicio == null) {
            // Esta validación sirve para no marcar como consumida si faltan datos. Con esto se depuran errores de datos.
            shouldConsume = false;
        } else if (fecha.isBefore(today)) {
            shouldConsume = true;
        } else if (fecha.equals(today) && (horaInicio.equals(now) || horaInicio.isBefore(now))) {
            shouldConsume = true;
        }

        //Aquí se marca como consumida si corresponde
        if (shouldConsume) {
            reserva.setStatusReserva(StatusReserva.CONSUMIDA);
            reservaRepository.save(reserva);
        }
    }

    // Tarea programada que corre cada minuto para revisar reservas pendientes y marcarlas como consumidas si corresponde.
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void scheduledConsumeCheck() {
        List<Reserva> pendientes = reservaRepository.findAllByStatusReserva(StatusReserva.RESERVADA);
        for (Reserva r : pendientes) {
            checkAndMarkConsumedIfNeeded(r);
        }
    }

    // Desde acá, el método para actualizar el estado de una reserva con las validaciones necesarias. No permite marcar como consumida manualmente ni tampoco cancelar reservas ya en curso o pasadas. Solo puede cambiar el estado si se es el dueño de la reserva. Habría que agregar de cierta manera, --popups o mensajes en el front para mostrar estos errores al usuario--.
    @Transactional
    public void updateStatus(Long idReserva, String statusStr, String usernameRequester) {
        Reserva reserva = reservaRepository.findById(idReserva).orElseThrow(NotFoundException::new);
        // validar dueño
        if (reserva.getUser() == null || !reserva.getUser().getUsername().equals(usernameRequester)) {
            throw new IllegalArgumentException("No tienes permisos sobre esta reserva");
        }

        // nuevo estado para asignar a la reserva
        StatusReserva newStatus;
        try {
            newStatus = StatusReserva.valueOf(statusStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido");
        }

        // reglas de negocio para los cambios de estado
        if (reserva.getStatusReserva() == StatusReserva.CONSUMIDA) {
            throw new IllegalArgumentException("No se puede cambiar el estado de una reserva ya consumida");
        }

        if (newStatus == StatusReserva.CONSUMIDA) {
            // no permitimos marcar manualmente como consumida, tal vez para un admin en el futuro debería de permitirse.
            throw new IllegalArgumentException("No es posible marcar manualmente como CONSUMIDA");
        }

        // si se intenta cancelar pero ya pasó la hora -> no permitir
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        if (newStatus == StatusReserva.CANCELADA) {
            if (reserva.getFechaReserva() != null) {
                // regla: no cancelar si ya está en el día y hora de inicio pasada (ya consumida)
                if (reserva.getFechaReserva().isBefore(today) ||
                   (reserva.getFechaReserva().equals(today) && (reserva.getHoraInicio().equals(now) || reserva.getHoraInicio().isBefore(now)))) {
                    throw new IllegalArgumentException("No se puede cancelar, la reserva ya está en curso o pasada");
                }
            }
        }

        reserva.setStatusReserva(newStatus);
        reservaRepository.save(reserva);
    }

    // Mapeo personalizado para incluir el nombre del amenity y el estado de la reserva, usado en "mis reservas". Sin embargo, este mapeo no reemplaza al otro usado en otros endpoints. Es importante aclarar que desde el método de crear usuarios no se esta registrando el idAmenity cuando se crea la reserva en el front, por lo que el nombre del amenity no se mostrará en ese caso. Hay que arreglar eso y posteriormente borrar esta aclaración.
    private ReservaDTO mapToDTOWithAmenityAndStatus(final Reserva reserva) {
        final ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getIdReserva());
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setHoraInicio(reserva.getHoraInicio());
        dto.setHoraFin(reserva.getHoraFin());
        dto.setNumeroPersonas(reserva.getNumeroPersonas());
        dto.setUser(reserva.getUser() == null ? null : reserva.getUser().getIdUser());
        dto.setAmenity(reserva.getAmenity() == null ? null : reserva.getAmenity().getIdAmenity());
        dto.setAmenityNombre(reserva.getAmenity() == null ? null : reserva.getAmenity().getNombre());
        dto.setStatusReserva(reserva.getStatusReserva());

        // amenityNombre — si llega a haber LazyInitializationException, de eso se tiene que encargar el @Transactional
        if (reserva.getAmenity() != null) {
            dto.setAmenityNombre(reserva.getAmenity().getNombre());
        } else {
            dto.setAmenityNombre(null);
        }

        // status — con el nuevo campo en Reserva
        dto.setStatusReserva(reserva.getStatusReserva()); // asume campo del tipo StatusReserva
        return dto;
    }



    //   @EventListener(BeforeDeleteReserva.class)
    //     public void on(final BeforeDeleteReserva event) {
    //     final ReferencedException referencedException = new ReferencedException();
    //     final Reserva AmenityReserva = reservaRepository.findFirstByAmenityIdAmenity(event.getIdAmenity());
    //     if (AmenityReserva != null) {
    //         referencedException.setKey("amenity.reserva.amenity.referenced");
    //         referencedException.addParam(AmenityReserva.getIdReserva());
    //         throw referencedException;
    //     }
    // }

}
