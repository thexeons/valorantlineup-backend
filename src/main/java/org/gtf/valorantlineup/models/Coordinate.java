package org.gtf.valorantlineup.models;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Coordinate {

    private Double x;

    private Double y;

    public Coordinate(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate() {

    }
}
