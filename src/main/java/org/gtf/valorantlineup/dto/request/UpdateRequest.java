package org.gtf.valorantlineup.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class UpdateRequest {

    @NotBlank
    private LineupEditRequest meta;

    private List<NodeRequest> nodes;

}
