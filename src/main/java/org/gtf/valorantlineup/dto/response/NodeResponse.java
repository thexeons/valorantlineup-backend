package org.gtf.valorantlineup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class NodeResponse {

    @JsonProperty("uuid_node")
    private String uuidNode;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("skillType")
    private String skillType;

    @JsonProperty("source")
    private HashMap<String,Double> source;

    @JsonProperty("destination")
    private HashMap<String,Double> destination;

    @JsonProperty("images")
    private List<ImageResponse> images;

    @JsonProperty("tags")
    private List<String> tags;

}
