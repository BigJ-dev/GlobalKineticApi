package com.global.kinetic.controllers;

import Projection.UserProjection;
import com.global.kinetic.repositories.UserRepository;
import com.global.kinetic.services.UserServiceImpl;
import com.global.kinetic.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserServiceImpl userService;
    private final UserRepository userRepo;

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProjection>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/activeUsers")
    public ResponseEntity<List<UserDetails>> getAllLoggedInUsers() {
        return ResponseEntity.ok().body(userService.getAllLoggedInUsers());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUserInfo(@PathVariable("id") Long userId, @RequestBody User user) {
        return ResponseEntity.ok().body(userService.updateUserDetails(user, userId));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String> > userSessionToken(@RequestHeader String authorization) {
        return ResponseEntity.ok().body(userService.getUserSessionToken(userRepo.findByUsername(userService.getUserNameByToken(authorization))));
    }

    @PostMapping("/logout/{id}")
    public ResponseEntity<Map<String, String>> getUserSessionTokenById(@PathVariable("id") Long userId) {
        Map<String, String> userDetails = userService.getUserSessionToken(userRepo.getById(userId));
        userDetails.remove("id");
        return ResponseEntity.ok().body(userDetails);
    }

    @GetMapping("/user/{userId}")
    public User getUserByUserId(@PathVariable Long userId) {
        return userService.getUserByUserId(userId);
    }

}
