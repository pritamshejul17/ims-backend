package io.bytecode.ims.respository;

import io.bytecode.ims.model.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifyTokenRepository extends JpaRepository<VerifyToken, Long> {
    Optional<VerifyToken> findByToken(String token);
}

