package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.Lineup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

@Repository
public interface AsyncLineupRepository extends BaseRepository<Lineup> {

    @Async
    @Query(value = "SELECT * FROM lineups l WHERE l.uuid = ?1", nativeQuery = true)
    public CompletableFuture<Lineup> findUuid(String uuid);

}
