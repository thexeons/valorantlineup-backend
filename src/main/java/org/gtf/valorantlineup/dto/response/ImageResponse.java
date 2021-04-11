package org.gtf.valorantineup.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageResponse {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("url")
    private String url;

}
