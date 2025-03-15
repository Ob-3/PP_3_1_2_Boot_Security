package ru.kata.springsecurity.repository;

import org.springframework.stereotype.Repository;
import ru.kata.springsecurity.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // Для поиска ролей по имени
}

// имплементировать данный интерфейс не нужно, т.к. Spring Data JPA сам создаст нужную реализацию!!!