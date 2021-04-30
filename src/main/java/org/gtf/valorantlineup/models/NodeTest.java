package org.gtf.valorantlineup.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "nodes")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Data
public class NodeTest extends Base{

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> properties = new HashMap<>();
}
