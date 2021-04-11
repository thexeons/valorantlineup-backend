package org.gtf.valorantineup.repositories;

import org.gtf.valorantineup.enums.ERole;
import org.gtf.valorantineup.models.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {
	Optional<Role> findByName(ERole name);

	boolean existsByName(ERole name);
}
