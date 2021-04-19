package org.gtf.valorantlineup.dto.request;

import lombok.Data;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PostRequest {

//    @NotNull: The CharSequence, Collection, Map or Array object is not null, but can be empty.
//    @NotEmpty: The CharSequence, Collection, Map or Array object is not null and size > 0.
//    @NotBlank: The string is not null and the trimmed length is greater than zero.

    @NotEmpty
    private LineupMetaRequest meta;

    @NotNull
    private List<NodeRequest> nodes;

}
