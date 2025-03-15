package ru.kata.springsecurity.repository;

import org.springframework.stereotype.Repository;
import ru.kata.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Для поиска пользователя по имени
}

// имплементировать данный интерфейс не нужно, т.к. Spring Data JPA сам создаст нужную реализацию!!!