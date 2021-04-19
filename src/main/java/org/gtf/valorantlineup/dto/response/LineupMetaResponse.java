package org.gtf.valorantlineup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LineupMetaResponse {

    @JsonProperty("uuid_lineup")
    private String uuidLineup;

    @JsonProperty("title")
    private String title;

    @JsonProperty("map")
    private String map;

}
