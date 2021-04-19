package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.enums.Peta;
import org.gtf.valorantlineup.models.Lineup;
import org.gtf.valorantlineup.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface LineupRepository extends BaseRepository<Lineup> {

    List<Lineup> findAllByUser(User user);

    Boolean existsByTitle(String title);

    @Query(value="select * from lineups a where a.title ilike %?1% and a.map = CAST(?2 as map_info)", nativeQuery=true)
    Page<Lineup> filterLineupEnum(String title, String map, Pageable pageable);

    @Query(value="select * from lineups a where a.title ilike %?1%", nativeQuery=true)
    Page<Lineup> filterLineup(String title, Pageable pageable);

}
