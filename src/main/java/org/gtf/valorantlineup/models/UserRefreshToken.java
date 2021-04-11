package org.gtf.valorantlineup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "refresh_token")
@Data
public class UserRefreshToken extends Base {

    @Column(nullable = false)
    private String token;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;

    public UserRefreshToken() {

    }

    public UserRefreshToken(String token, Date expiryDate, User user) {
        this.token = token;
        this.expiryDate = expiryDate;
        this.user = user;
    }
}