package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.Lineup;
import org.gtf.valorantlineup.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LineupRepository extends BaseRepository<Lineup> {

    List<Lineup> findAllByUser(User user);

    Boolean existsByTitle(String title);

}
