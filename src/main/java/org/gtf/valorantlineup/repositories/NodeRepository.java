package org.gtf.valorantineup.repositories;

import org.gtf.valorantineup.models.Node;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends BaseRepository<Node> {

    List<Node> findAllByLineupUuid(String uuid);

    List<Node> deleteAllByLineupUuid(String uuid);


}
