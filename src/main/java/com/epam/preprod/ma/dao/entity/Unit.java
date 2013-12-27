package com.epam.preprod.ma.dao.entity;

/**
 * User entity.
 * 
 * @author Leonid Polyakov
 * 
 * @version 1.0
 * 
 */
public class Unit extends AEntity {

	private String name;

	private Unit parent;

	private User rm;

	private boolean deleted;

	public Unit() {
	}

	public Unit(String name, Unit parent, User rm, boolean deleted) {
		this.name = name;
		this.parent = parent;
		this.rm = rm;
		this.deleted = deleted;
	}

	public Unit(Long id, String name, Unit parent, User rm, boolean deleted) {
		this.id = id;
		this.name = name;
		this.parent = parent;
		this.rm = rm;
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Unit getParent() {
		return parent;
	}

	public void setParent(Unit parent) {
		this.parent = parent;
	}

	public User getRm() {
		return rm;
	}

	public void setRm(User rm) {
		this.rm = rm;
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
		Unit other = (Unit) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", name=" + name + ", parent=" + parent
				+ ", rm=" + rm + ", deleted=" + deleted + "]";
	}
}