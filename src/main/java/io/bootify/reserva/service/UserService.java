    package io.bootify.reserva.service;

    import io.bootify.reserva.domain.Role;
    import io.bootify.reserva.domain.Status;
    import io.bootify.reserva.domain.User;
    import io.bootify.reserva.events.BeforeDeleteUser;
    import io.bootify.reserva.model.ChangePasswordRequest;
    import io.bootify.reserva.model.UserDTO;
    import io.bootify.reserva.model.UserRegisterDTO;
    import io.bootify.reserva.repos.UserRepository;
    import io.bootify.reserva.util.NotFoundException;
    import io.jsonwebtoken.security.Password;
    import jakarta.validation.Valid;

    import java.util.List;
    import org.springframework.context.ApplicationEventPublisher;
    import org.springframework.data.domain.Sort;
    import org.springframework.stereotype.Service;
    import org.springframework.validation.BindingResult;
    import org.springframework.security.core.userdetails.UserDetails;
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

        public void updateUser(final Long idUser, final UserDTO userDTO) {
            User user = userRepository.findById(idUser)
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
            userDTO.setCorreo(user.getCorreo());
            return userDTO;
        }

        private UserRegisterDTO mapToDTORegister(final User user, final UserRegisterDTO userRegisterDTO) {
            user.setIdUser(userRegisterDTO.getIdUser());
            user.setNombre(userRegisterDTO.getNombre());
            user.setApellido(userRegisterDTO.getApellido());
            user.setTipoDocumento(userRegisterDTO.getTipoDocumento());
            user.setNumeroDocumento(userRegisterDTO.getNumeroDocumento());
            user.setTelefono(userRegisterDTO.getTelefono());
            user.setCorreo(userRegisterDTO.getCorreo());
            user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
            user.setRole(Role.USER);
            user.setStatus(Status.ACTIVO);
            return userRegisterDTO;
        }

        private User mapToEntityUser(final UserDTO userDTO, final User user) {
            user.setNombre(userDTO.getNombre());
            user.setApellido(userDTO.getApellido());
            user.setTipoDocumento(userDTO.getTipoDocumento());
            user.setNumeroDocumento(userDTO.getNumeroDocumento());
            user.setTelefono(userDTO.getTelefono());
            user.setCorreo(userDTO.getCorreo());
            return user;
        }

        private User mapToEntityRegister(final UserRegisterDTO userRegisterDTO, final User user) {
            user.setNombre(userRegisterDTO.getNombre());
            user.setApellido(userRegisterDTO.getApellido());
            user.setTipoDocumento(userRegisterDTO.getTipoDocumento());
            user.setNumeroDocumento(userRegisterDTO.getNumeroDocumento());
            user.setTelefono(userRegisterDTO.getTelefono());
            user.setCorreo(userRegisterDTO.getCorreo());
            user.setUsername(userRegisterDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
            user.setRole(Role.USER);
            user.setStatus(Status.ACTIVO);
            return user;
        }

        public UserDTO getCurrentUser(UserDetails userDetails) {
            return userRepository.findByUsername(userDetails.getUsername())
                    .map(user -> mapToDTOUser(user, new UserDTO()))
                    .orElseThrow(NotFoundException::new);
        }

        public void deactivateUser(Long id) {
            User user = userRepository.findById(id)
                    .orElseThrow(NotFoundException::new);
            user.setStatus(Status.INACTIVO);
            userRepository.save(user);
            if (user.getStatus() == Status.INACTIVO) return;
        }

        public void changePassword(UserDetails userDetails, String currentPassword, String newPassword, String confirmPassword) {
            User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("El usuario no fue encontrado"));

            if(!passwordEncoder.matches(currentPassword, user.getPassword())){
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }

            if(!newPassword.equals(confirmPassword)){
                throw new IllegalArgumentException("La nueva contraseña y la confirmación no coinciden");
            }

            if(passwordEncoder.matches(newPassword, user.getPassword())){
                throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual");

            }

                // 4️⃣ Validaciones de seguridad básica
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
            }

            if (!newPassword.matches(".*[A-Z].*")) {
                throw new IllegalArgumentException("La nueva contraseña debe contener al menos una letra mayúscula");
            }

            if (!newPassword.matches(".*[a-z].*")) {
                throw new IllegalArgumentException("La nueva contraseña debe contener al menos una letra minúscula");
            }

            if (!newPassword.matches(".*\\d.*")) {
                throw new IllegalArgumentException("La nueva contraseña debe contener al menos un número");
            }

            if (!newPassword.matches(".*[!@#$%^&*()_+\\-={}:;\"'<>,.?/].*")) {
                throw new IllegalArgumentException("La nueva contraseña debe contener al menos un carácter especial");
            }

            // Finalmmente, se cifra la contraseña y se guarda.
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }

        public boolean existsByCorreo(String correo) {
            return userRepository.existsByCorreo(correo);
        }

        public boolean existsByUsername(String username) {
            return userRepository.existsByUsername(username);
        }

    }
