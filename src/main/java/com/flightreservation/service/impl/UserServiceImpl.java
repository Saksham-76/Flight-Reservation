package com.flightreservation.service.impl;

import com.flightreservation.dto.LoginForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.exception.InvalidCredentialsException;
import com.flightreservation.mapper.UserMapper;
import com.flightreservation.model.User;
import com.flightreservation.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final String INSERT_USER =
            "INSERT INTO `user` (email, password, role) VALUES (:email, :password, :role)";
    private static final String FIND_BY_EMAIL =
            "SELECT user_id, email, password, role FROM `user` WHERE email = :email";
    private static final String COUNT_BY_EMAIL =
            "SELECT COUNT(*) FROM `user` WHERE email = :email";

    private final NamedParameterJdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(NamedParameterJdbcTemplate jdbc,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User register(SignupForm form) {
        User user = userMapper.toEntity(form, passwordEncoder.encode(form.getPassword()));

        if (existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(INSERT_USER, new BeanPropertySqlParameterSource(user), keyHolder);
        } catch (DuplicateKeyException e) {
            // two people registering the same email at the same moment: the UNIQUE key wins
            throw new DuplicateResourceException("An account with this email already exists");
        }
        user.setUserId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public SessionUser login(LoginForm form) {
        User user = findByEmail(form.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return userMapper.toSessionUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        List<User> users = jdbc.query(FIND_BY_EMAIL,
                Map.of("email", email.trim().toLowerCase()),
                new BeanPropertyRowMapper<>(User.class));
        return users.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(COUNT_BY_EMAIL,
                Map.of("email", email.trim().toLowerCase()), Integer.class);
        return count != null && count > 0;
    }
}