package io.bootify.reserva.service;

import io.bootify.reserva.domain.Reserva;
import io.bootify.reserva.domain.User;
import io.bootify.reserva.events.BeforeDeleteReserva;
import io.bootify.reserva.events.BeforeDeleteUser;
import io.bootify.reserva.model.ReservaDTO;
import io.bootify.reserva.repos.ReservaRepository;
import io.bootify.reserva.repos.UserRepository;
import io.bootify.reserva.util.NotFoundException;
import io.bootify.reserva.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public ReservaService(final ReservaRepository reservaRepository,
            final UserRepository userRepository, final ApplicationEventPublisher publisher) {
        this.reservaRepository = reservaRepository;
        this.userRepository = userRepository;
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

}
