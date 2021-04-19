package org.gtf.valorantlineup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LineupNodeResponse {

    @JsonProperty("meta")
    private LineupMetaResponse meta;

    @JsonProperty("nodes")
    private List<NodeResponse> nodes;

}
