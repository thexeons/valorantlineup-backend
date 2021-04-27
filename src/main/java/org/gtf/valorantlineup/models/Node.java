package org.gtf.valorantlineup.models;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "nodes")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
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

    @Column(name = "tags", columnDefinition = "text[]")
    @Type(type = "list-array")
    private List<String> tags;


}
