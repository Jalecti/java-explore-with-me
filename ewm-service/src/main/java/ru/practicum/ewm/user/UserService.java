package ru.practicum.ewm.user;

import org.springframework.data.domain.Pageable;
import java.util.Collection;


public interface UserService {

    UserDto save(NewUserRequest newUserRequest);

    Collection<UserDto> findAll(Pageable pageable);

    Collection<UserDto> findAllByParams(Collection<Long> ids, Pageable pageable);

    UserDto findUserById(Long userId);

    void delete(Long userId);

    User checkUser(Long userId);

}
