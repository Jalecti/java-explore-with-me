package ru.practicum.ewm.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userEmail);

    Page<User> findByIdIn(Collection<Long> ids, Pageable pageable);

}
