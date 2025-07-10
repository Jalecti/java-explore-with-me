package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictEmailException;
import ru.practicum.ewm.exception.NotFoundException;

import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Transactional
    @Override
    public UserDto save(NewUserRequest newUserRequest) {
        checkUserEmail(newUserRequest.getEmail());
        User user = UserMapper.mapToUser(newUserRequest);
        user = userStorage.save(user);
        log.info("User {} is registered with the ID: {}", newUserRequest.getEmail(), user.getId());
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public Collection<UserDto> findAll(Pageable pageable) {
        return userStorage.findAll(pageable)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<UserDto> findAllByIds(Collection<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return findAll(pageable);
        }

        return userStorage.findByIdIn(ids, pageable)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long userId) {
        return UserMapper.mapToUserDto(checkUser(userId));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        User userToDelete = checkUser(userId);
        String email = userToDelete.getEmail();
        userStorage.deleteById(userId);
        log.info("User {} with the ID: {} has been deleted", email, userId);
    }

    public User checkUser(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> {
            log.error("The user was not found with the ID: {}", userId);
            return new NotFoundException("The user was not found with the ID: " + userId);
        });
    }

    private void checkUserEmail(String userEmail) {
        if (userStorage.findByEmail(userEmail).isPresent()) {
            log.error("The user with the specified email has already been registered: {}", userEmail);
            throw new ConflictEmailException("The user with the specified email has already been registered: " + userEmail);
        }
    }
}
