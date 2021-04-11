package org.gtf.valorantineup.models;

import lombok.Data;
import org.gtf.valorantineup.enums.ERole;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Data
public class Role extends Base {

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;

}