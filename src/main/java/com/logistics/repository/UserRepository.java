package com.logistics.repository;

import com.logistics.model.User;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
}
