package com.valek.service.mail;

import com.valek.model.PasswordResetToken;
import com.valek.model.User;
import com.valek.repository.PasswordResetTokenRepository;
import com.valek.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    static final int CodeExpiredTime = 10;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void sendResetCode(String email) {

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElse(null);

        if (user == null) {
            return;
        }

        String code = String.format(
                Locale.ROOT,
                "%06d",
                random.nextInt(1_000_000)
        );

        PasswordResetToken token = new PasswordResetToken();

        token.setEmail(user.getEmail());
        token.setCode(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(CodeExpiredTime));

        tokenRepository.deleteByEmail(user.getEmail());
        tokenRepository.save(token);

        emailService.sendEmail(
                user.getEmail(),
                "Восстановление пароля Unolingo",
                "Ваш код восстановления пароля: " + code
                        + "\n\nКод действителен в течение " + CodeExpiredTime + " минут."
        );
    }

    @Transactional
    public void resetPassword(
            String email,
            String code,
            String newPassword
    ) {

        if (email == null || code == null || newPassword == null) {
            throw new IllegalArgumentException("Заполните все поля.");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Пароль должен содержать минимум 8 символов."
            );
        }

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Неверный код или email."
                ));

        PasswordResetToken token = tokenRepository
                .findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Код восстановления не найден."
                ));

        if (!token.getCode().equals(code.trim())) {
            throw new IllegalArgumentException("Неверный код.");
        }

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            tokenRepository.delete(token);

            throw new IllegalArgumentException(
                    "Срок действия кода истёк."
            );
        }

        String passwordHash = BCrypt.hashpw(
                newPassword,
                BCrypt.gensalt(12)
        );

        user.setPasswordHash(passwordHash);
        userRepository.save(user);

        tokenRepository.delete(token);
    }

}
