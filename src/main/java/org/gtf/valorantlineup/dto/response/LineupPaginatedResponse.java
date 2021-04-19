package org.gtf.valorantlineup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LineupPaginatedResponse {
    @JsonProperty("lineup")
    private List<LineupMetaResponse> lineups;

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("totalPage")
    private int totalPage;

    @JsonProperty("totalElements")
    private long totalElements;

    @JsonProperty("hasContent")
    private boolean hasContent;

    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    @JsonProperty("hasNext")
    private boolean hasNext;
}
