package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.User;
import org.gtf.valorantlineup.models.UserRefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends BaseRepository<UserRefreshToken> {

    Optional<UserRefreshToken> findByToken(String token);

    Optional<UserRefreshToken> findByUser(User user);

}
