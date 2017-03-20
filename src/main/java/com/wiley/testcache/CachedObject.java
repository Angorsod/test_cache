package com.wiley.testcache;

import java.io.Serializable;

/**
 * 
 * @author agorshunov
 *
 * This is a class that represent caching object. Not a standard Java Object class because we need to redefine hashCode and equal.
 * 
 */
public class CachedObject implements Serializable {
	private static final long serialVersionUID = -424244618639833919L;

	private static int count = 0;
	
	private final long id;
	
	public CachedObject() {
		synchronized (this) {
			id = count++;
		}
	}

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "CachedObject [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		CachedObject other = (CachedObject) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
