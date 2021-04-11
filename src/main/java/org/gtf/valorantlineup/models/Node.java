package org.gtf.valorantineup.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "nodes")
@Data
public class Node extends Base {

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "lineup_id")
    private Lineup lineup;

    @Embedded
    @AttributeOverrides({@AttributeOverride( name = "x", column = @Column(name = "sourceX")),
                         @AttributeOverride( name = "y", column = @Column(name = "sourceY"))})
    Coordinate source;

    @Embedded
    @AttributeOverrides({@AttributeOverride( name = "x", column = @Column(name = "destinationX")),
                         @AttributeOverride( name = "y", column = @Column(name = "destinationY"))})
    Coordinate destination;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "skillType")
    private String skillType;

}
