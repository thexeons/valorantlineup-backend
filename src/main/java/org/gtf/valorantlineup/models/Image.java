package org.gtf.valorantlineup.models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "images")
@Data
public class Image extends Base{

    @Column(name="ORIGINAL_NAME")
    private String originalName;

    @Column(name="SAVED_NAME")
    private String savedName;

    @Column(name="FILE_SIZE")
    private long fileSize;

    private String url;

    @ManyToOne
    @JoinColumn(name = "node_id")
    Node node;

}