package org.gtf.valorantlineup.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import org.gtf.valorantlineup.enums.ERole;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(	name = "users",
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "username"),
			@UniqueConstraint(columnNames = "email")
		},
		indexes = {
				@Index(name = "un_index", columnList = "username"),
				@Index(name = "em_index", columnList = "email")
		})
@Data
public class User extends Base {

	@NotBlank
	@Size(max = 20)
	private String username;

	@Size(max = 50)
	private String email;

	@NotBlank
	@Size(max = 120)
	private String password;

	private boolean enabled;

	private Boolean immediateChangePassword;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;

	@ManyToMany(cascade ={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinTable(	name = "user_roles",
				joinColumns = @JoinColumn(name = "user_id", nullable = false),
				inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<org.gtf.valorantlineup.models.Role> roles = new HashSet<>();

	public User() {

	}

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.enabled = true;
		this.immediateChangePassword = true;
	}

	public Set<ERole> getERoles() {
		Set<ERole> roleList = new HashSet<>();
		for (Role role : roles) {
			roleList.add(role.getName());
		}
		return roleList;
	}
}
