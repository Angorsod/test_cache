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
	private final String name;
	private final int age;
	
	public CachedObject(long id, String name, int age) {
		synchronized (this) {
			count++;
		}
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public static int getCount() {
		return count;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	@Override
	public String toString() {
		return "CachedObject [id=" + id + ", name=" + name + ", age=" + age + "]";
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
