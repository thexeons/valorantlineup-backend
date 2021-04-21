package org.gtf.valorantlineup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NodeRequest {

    private String title;

    private String description;

    @NotBlank
    private String skillType;

    @NotBlank
    private double sourceX;

    @NotBlank
    private double sourceY;

    private Double destinationX;

    private Double destinationY;

    private String[] imageId;
}
