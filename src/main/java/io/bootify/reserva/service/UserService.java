package io.bootify.reserva.service;

import io.bootify.reserva.domain.Role;
import io.bootify.reserva.domain.User;
import io.bootify.reserva.events.BeforeDeleteUser;
import io.bootify.reserva.model.UserDTO;
import io.bootify.reserva.model.UserRegisterDTO;
import io.bootify.reserva.repos.UserRepository;
import io.bootify.reserva.util.NotFoundException;
import io.jsonwebtoken.security.Password;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;

/*     public UserService(UserRepository userRepository, ApplicationEventPublisher publisher, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.passwordEncoder = passwordEncoder;
    } */

    
    public UserDTO get(final Long idUser) {
        return userRepository.findById(idUser)
        .map(user -> mapToDTOUser(user, new UserDTO()))
        .orElseThrow(NotFoundException::new);
    }

    public UserRegisterDTO getRegister(final Long idUser) {
        return userRepository.findById(idUser)
        .map(user -> mapToDTORegister(user, new UserRegisterDTO()))
        .orElseThrow(NotFoundException::new);
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("idUser"));
        return users.stream()
                .map(user -> mapToDTOUser(user, new UserDTO()))
                .toList();
    }

    public Long create(final UserRegisterDTO userRegisterDTO) {
        final User user = new User();
        mapToEntityRegister(userRegisterDTO, user);
        return userRepository.save(user).getIdUser();
    }

    public void update(final Long idUser, final UserDTO userDTO) {
        final User user = userRepository.findById(idUser)
                .orElseThrow(NotFoundException::new);
        mapToEntityUser(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Long idUser) {
        final User user = userRepository.findById(idUser)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteUser(idUser));
        userRepository.delete(user);
    }

    private UserDTO mapToDTOUser(final User user, final UserDTO userDTO) {
        userDTO.setIdUser(user.getIdUser());
        userDTO.setNombre(user.getNombre());
        userDTO.setApellido(user.getApellido());
        userDTO.setTipoDocumento(user.getTipoDocumento());
        userDTO.setNumeroDocumento(user.getNumeroDocumento());
        userDTO.setTelefono(user.getTelefono());
        return userDTO;
    }

    private UserRegisterDTO mapToDTORegister(final User user, final UserRegisterDTO userRegisterDTO) {
        user.setIdUser(userRegisterDTO.getIdUser());
        user.setNombre(userRegisterDTO.getNombre());
        user.setApellido(userRegisterDTO.getApellido());
        user.setTipoDocumento(userRegisterDTO.getTipoDocumento());
        user.setNumeroDocumento(userRegisterDTO.getNumeroDocumento());
        user.setTelefono(userRegisterDTO.getTelefono());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(Role.USER);
        return userRegisterDTO;
    }

    private User mapToEntityUser(final UserDTO userDTO, final User user) {
        user.setNombre(userDTO.getNombre());
        user.setApellido(userDTO.getApellido());
        user.setTipoDocumento(userDTO.getTipoDocumento());
        user.setNumeroDocumento(userDTO.getNumeroDocumento());
        user.setTelefono(userDTO.getTelefono());
        return user;
    }

    private User mapToEntityRegister(final UserRegisterDTO userRegisterDTO, final User user) {
        user.setNombre(userRegisterDTO.getNombre());
        user.setApellido(userRegisterDTO.getApellido());
        user.setTipoDocumento(userRegisterDTO.getTipoDocumento());
        user.setNumeroDocumento(userRegisterDTO.getNumeroDocumento());
        user.setTelefono(userRegisterDTO.getTelefono());
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(Role.USER);
        return user;
    }

}
