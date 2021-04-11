package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.enums.ERole;
import org.gtf.valorantlineup.models.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {
	Optional<Role> findByName(ERole name);

	boolean existsByName(ERole name);
}
