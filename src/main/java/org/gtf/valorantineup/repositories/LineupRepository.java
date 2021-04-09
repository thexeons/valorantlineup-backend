package org.gtf.valorantineup.repositories;

import org.gtf.valorantineup.models.Lineup;
import org.gtf.valorantineup.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineupRepository extends BaseRepository<Lineup> {

    List<Lineup> findAllByUser(User user);

    Boolean existsByTitle(String title);

}
