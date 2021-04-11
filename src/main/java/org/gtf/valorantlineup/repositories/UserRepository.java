package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.enums.ERole;
import org.gtf.valorantlineup.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Optional<User> findByEmail(String email);

	User findByEmailAndEnabledTrue(String email);

	Boolean existsByEmail(String email);

	List<User> findAllByRolesName(ERole name);
}
