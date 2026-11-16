
package com.valek.service;

import com.valek.model.User;
import com.valek.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public void register(
            String username,
            String email,
            String password,
            String level
    ) {

        username = username.trim();
        email = email.trim().toLowerCase();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Заполните все поля.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException(
                    "Введите корректный адрес электронной почты."
            );
        }

        if (username.length() > 50) {
            throw new IllegalArgumentException(
                    "Имя пользователя не должно превышать 50 символов."
            );
        }

        if (email.length() > 255) {
            throw new IllegalArgumentException(
                    "Email слишком длинный."
            );
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException(
                    "Пароль должен содержать минимум 8 символов."
            );
        }

        if (level == null || !level.matches("A[12]|B[12]|C[12]")) {
            throw new IllegalArgumentException(
                    "Выберите корректный уровень английского."
            );
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Этот email уже зарегистрирован."
            );
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException(
                    "Это имя пользователя уже занято."
            );
        }

        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(12)
        );

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole("USER");
        user.setLevel(level);

        userRepository.save(user);
    }


    public User login(String login, String password) {

        if (login == null || login.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        User user = userRepository.findByLogin(login.trim())
                .orElse(null);

        if (user == null) {
            return null;
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            return null;
        }

        return user;
    }
}
