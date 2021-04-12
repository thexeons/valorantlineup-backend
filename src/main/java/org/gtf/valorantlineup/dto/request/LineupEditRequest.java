package org.gtf.valorantlineup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LineupEditRequest {

    @NotBlank
    private String title;

}
