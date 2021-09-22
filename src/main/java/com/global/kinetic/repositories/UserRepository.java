package com.global.kinetic.repositories;

import Projection.UserProjection;
import com.global.kinetic.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select * from user", nativeQuery = true)
    List<UserProjection> getAllUsers();

    User findByUsername(String username);

}
