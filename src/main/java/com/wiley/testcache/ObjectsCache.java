package com.wiley.testcache;

public interface ObjectsCache<K, T> {

	T get(K id);
	void put(K id, T obj);
	void invalidate(K id);
	void clear();
	int getCount();
	
	enum CacheStrategy {
		LRU, // Least recently used
		MRU, // Most Recently Used
		LFU  // Least Frequently Used
	}
	
	void setStrategy(CacheStrategy strategy);
	CacheStrategy getStrategy();
	void setMaxSize(int maxSize);
	int getMaxSize();
}
