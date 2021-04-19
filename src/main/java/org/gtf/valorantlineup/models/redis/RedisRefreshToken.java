package org.gtf.valorantlineup.models.redis;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RedisRefreshToken implements Serializable {
    private static final long serialVersionUID = 3396341834518109230L;

    private String userId;
    private String username;
    private String token;
    private Date expiryDate;

}
