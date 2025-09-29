package io.bootify.reserva.service;

import io.bootify.reserva.domain.User;
import io.bootify.reserva.events.BeforeDeleteUser;
import io.bootify.reserva.model.UserDTO;
import io.bootify.reserva.repos.UserRepository;
import io.bootify.reserva.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;

    public UserService(final UserRepository userRepository,
            final ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.publisher = publisher;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("idUser"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Long idUser) {
        return userRepository.findById(idUser)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getIdUser();
    }

    public void update(final Long idUser, final UserDTO userDTO) {
        final User user = userRepository.findById(idUser)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long idUser) {
        final User user = userRepository.findById(idUser)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUser(idUser));
        userRepository.delete(user);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setIdUser(user.getIdUser());
        userDTO.setNombre(user.getNombre());
        userDTO.setApellido(user.getApellido());
        userDTO.setTipoDocumento(user.getTipoDocumento());
        userDTO.setNumeroDocumento(user.getNumeroDocumento());
        userDTO.setTelefono(user.getTelefono());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setNombre(userDTO.getNombre());
        user.setApellido(userDTO.getApellido());
        user.setTipoDocumento(userDTO.getTipoDocumento());
        user.setNumeroDocumento(userDTO.getNumeroDocumento());
        user.setTelefono(userDTO.getTelefono());
        return user;
    }

}
