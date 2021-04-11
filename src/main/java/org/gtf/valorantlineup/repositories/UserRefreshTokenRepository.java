package org.gtf.valorantineup.repositories;

import org.gtf.valorantineup.models.User;
import org.gtf.valorantineup.models.UserRefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends BaseRepository<UserRefreshToken> {

    Optional<UserRefreshToken> findByToken(String token);

    Optional<UserRefreshToken> findByUser(User user);

}
