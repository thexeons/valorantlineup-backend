package org.gtf.valorantlineup.dto.request;

import lombok.Data;
import org.gtf.valorantlineup.models.Coordinate;

import javax.validation.constraints.NotBlank;

@Data
public class NodeRequest {

    private String title;

    private String description;

    @NotBlank
    private String skillType;

//    @NotBlank
//    private double sourceX;
//
//    @NotBlank
//    private double sourceY;

    @NotBlank
    private Coordinate source;

    private Coordinate destination;

//    private Double destinationX;
//
//    private Double destinationY;

    private ImageRequest[] images;

    private String[] tags;
}
