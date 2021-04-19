package org.gtf.valorantlineup.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.gtf.valorantlineup.enums.Peta;

import javax.validation.constraints.NotBlank;

@Data
public class LineupMetaRequest {

    @NotBlank
    private String title;

    @NotBlank
    @Schema(implementation = Peta.class)
    private String map;
}
