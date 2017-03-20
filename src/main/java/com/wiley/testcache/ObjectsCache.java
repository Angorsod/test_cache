package com.wiley.testcache;

public interface ObjectsCache {

	Object get(long id);
	void put(long id, Object obj);
	void invalidate(long id);
	
	enum CacheStrategy {
		LRU, // Least recently used
		MRU, // Most Recently Used
		LFU  // Least Frequently Used
	}
	
	void setStrategy(byte level, CacheStrategy strategy);
	CacheStrategy getStrategy(byte level);
	void setMaxSize(byte level, int maxSize);
	int getMaxSize(byte level);
}
