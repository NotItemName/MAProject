package com.epam.preprod.ma.dao.entity;

/**
 * User entity.
 * 
 * @author Oleksandr Lagoda, Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public class User extends AEntity {

	private String name;

	private Role role;

	private String email;

	private Unit unit;

	private boolean deleted;

	public User() {
	}

	public User(String name, Role role, String email, Unit unit, boolean deleted) {
		this.name = name;
		this.role = role;
		this.email = email;
		this.unit = unit;
		this.deleted = deleted;
	}

	public User(Long id, String name, Role role, String email, Unit unit,
			boolean deleted) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.email = email;
		this.unit = unit;
		this.deleted = deleted;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public Role getRole() {
		return role;
	}

	public String getEmail() {
		return email;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", role=" + role
				+ ", email=" + email + ", unit=" + unit + ", deleted="
				+ deleted + "]";
	}
}