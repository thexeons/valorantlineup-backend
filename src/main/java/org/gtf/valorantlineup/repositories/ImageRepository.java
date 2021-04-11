package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.Image;
import org.gtf.valorantlineup.models.Node;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends BaseRepository<Image> {

    List<Image> findAllByNode(Node node);

}
