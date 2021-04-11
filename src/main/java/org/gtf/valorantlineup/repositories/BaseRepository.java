package org.gtf.valorantlineup.repositories;

import org.gtf.valorantlineup.models.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

//Repository interface hierarchy :
//Base Repository -> JpaRepository -> PagingAndSortingRepository -> Repository
//Note: Use JpaRepository in case you need saveAndFlush() method

@NoRepositoryBean
public interface BaseRepository<T extends Base> extends JpaRepository<T, Long> {

    public T findByUuid(String uuid);

    public boolean existsByUuid(String uuid);

}