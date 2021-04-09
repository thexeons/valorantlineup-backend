package org.gtf.valorantineup.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "lineups")
@Data
public class Lineup extends Base {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "lineup",cascade = CascadeType.ALL) //mappedBy refers to the entity name on the other side
    private List<Node> nodes;

    @Column(name = "title")
    private String title;

    @Column(name = "map")
    private String map;

}
