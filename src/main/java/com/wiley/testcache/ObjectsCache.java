package com.wiley.testcache;

public interface ObjectsCache {

	Object get(long id);
	void put(long id, Object obj);
	void invalidate(long id);
	void clear();
	
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
