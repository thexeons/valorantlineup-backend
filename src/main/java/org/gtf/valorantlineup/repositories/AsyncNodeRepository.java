package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.Lineup;
import org.gtf.valorantlineup.models.Node;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public interface AsyncNodeRepository extends BaseRepository<Node> {

    @Async
    CompletableFuture<List<Node>> findAllByLineupUuid(String uuid);

}
