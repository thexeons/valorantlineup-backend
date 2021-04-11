package org.gtf.valorantineup.repositories;

import org.gtf.valorantineup.models.Image;
import org.gtf.valorantineup.models.Lineup;
import org.gtf.valorantineup.models.Node;
import org.gtf.valorantineup.models.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends BaseRepository<Image> {

    List<Image> findAllByNode(Node node);

}
