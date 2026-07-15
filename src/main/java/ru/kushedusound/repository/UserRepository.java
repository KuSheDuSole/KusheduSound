package ru.kushedusound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kushedusound.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
