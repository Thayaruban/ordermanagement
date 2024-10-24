package com.zerobeta.assignment.ordermanagement.repository;

import com.zerobeta.assignment.ordermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
