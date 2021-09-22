package com.global.kinetic.services;

import Projection.UserProjection;
import com.global.kinetic.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Map;

public interface UserService {

    User saveUser(User user);

    User getUserByUserId(Long userId);

    User updateUserDetails(User user, Long userId);

    Map<String, String>  getUserSessionToken(User user);

    List<UserProjection> getUsers();

    List<UserDetails> getAllLoggedInUsers();

    String getUserNameByToken(String token);

}
