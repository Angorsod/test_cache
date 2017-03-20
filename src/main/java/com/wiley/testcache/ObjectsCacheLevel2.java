package com.wiley.testcache;

public class ObjectsCacheLevel2<K, T> implements ObjectsCache<K, T> {

	private final ObjectsCache<K, T> level1Cache;

	private ObjectsCache.CacheStrategy strategy;
	private int maxSize;
	
	public ObjectsCacheLevel2(ObjectsCache<K, T> lvl1Cache) {
		level1Cache = lvl1Cache;
		maxSize = 100;
		strategy = CacheStrategy.LFU;
	}

	@Override
	public T get(K id) {
		return level1Cache.get(id);
	}

	@Override
	public void put(K id, T obj) {
		level1Cache.put(id, obj);
	}

	@Override
	public void invalidate(K id) {
		level1Cache.invalidate(id);
	}

	@Override
	public void clear() {
		level1Cache.clear();
	}

	@Override
	public int getCount() {
		return level1Cache.getCount();
	}

	@Override
	public void setStrategy(ObjectsCache.CacheStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public ObjectsCache.CacheStrategy getStrategy() {
		return strategy;
	}

	@Override
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

}
