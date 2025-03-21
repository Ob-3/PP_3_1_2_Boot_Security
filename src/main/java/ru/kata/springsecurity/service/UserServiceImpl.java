package ru.kata.springsecurity.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kata.springsecurity.entity.User;
import ru.kata.springsecurity.entity.Role;
import ru.kata.springsecurity.repository.RoleRepository;
import ru.kata.springsecurity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    @Transactional
    public User save(User user) {
        if (!user.getPassword().startsWith("$2a$")) { // Проверяем, зашифрован ли пароль
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"))));
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void update(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        existingUser.setUsername(user.getUsername());

        // Обновляем пароль только если он не пустой и не зашифрован
        if (user.getPassword() != null && !user.getPassword().isBlank() && !user.getPassword().startsWith("$2a$")) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Проверяем, были ли переданы роли, иначе оставляем старые
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> updatedRoles = user.getRoles().stream()
                    .map(role -> roleService.findById(role.getId())
                            .orElseThrow(() -> new RuntimeException("Роль не найдена")))
                    .collect(Collectors.toSet());
            existingUser.setRoles(updatedRoles);
        }
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));


        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

}
