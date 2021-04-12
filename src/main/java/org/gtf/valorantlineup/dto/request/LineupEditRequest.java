package org.gtf.valorantlineup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LineupRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String map;
}
