package com.global.kinetic.services;

import Projection.UserProjection;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.global.kinetic.models.User;
import com.global.kinetic.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    @Qualifier("sessionRegistry")
    private SessionRegistry sessionRegistry;

    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User getUserByUserId(Long userId) {
        return userRepo.getById(userId);
    }

    @Override
    public User updateUserDetails(User user, Long userId) {

        User existingUser = userRepo.getById(userId);
        if (existingUser.getId().equals(userId)) {
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(user.getPassword());
            existingUser.setPhone(user.getPhone());
        } else {
            log.error("This user don't exist in the db");
        }
        return userRepo.getById(userId);
    }

    @Override
    public Map<String, String> getUserSessionToken(User user) {
        User existingUser = userRepo.getById(user.getId());
        String sessionToken = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> userDetails = new HashMap<>();

        if (existingUser == null) {
            log.error("No matching user with the id you provided");
        } else {
            userDetails.put("sessionToken", sessionToken);
            userDetails.put("id", existingUser.getId().toString());
        }
        return userDetails;
    }

    @Override
    public List<UserProjection> getUsers() {
        return userRepo.getAllUsers();
    }

    @Override
    public List<UserDetails> getAllLoggedInUsers() {
        List<UserDetails> loggedInUsers = sessionRegistry.getAllPrincipals()
                .stream()
                .filter(principal -> principal instanceof UserDetails)
                .map(UserDetails.class::cast)
                .collect(Collectors.toList());
        if (loggedInUsers.isEmpty()) {
            log.error("No active user available");
        }
        return loggedInUsers;
    }

    @Override
    public String getUserNameByToken(String authorization) {
        String token = authorization.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            log.error("User not found in the db");
            throw new UsernameNotFoundException(username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }
}
