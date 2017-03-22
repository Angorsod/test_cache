package com.wiley.testcache;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ObjectsCacheMemoryImpl<K, T> implements ObjectsCache<K, T> {

//	private final static ObjectsCacheMemoryImpl instance = new ObjectsCacheMemoryImpl();

	private final Map<K, T> cache;
	private ObjectsCache.CacheStrategy strategy;
	private int maxSize;
	
	/**
	 * Read/write lock
	 */
	private final RWLock cacheLock;
	private final RWLock accessLock;
	
	/**
	 * Access map is used to an implement different cache strategies
	 */
	private class Access<I> {
		I id;
		long time;
		int count;
		public Access(I id) {
			// time = System.nanoTime();
			count = 0;
			this.id = id;
		}
	}
	private final Map<K, Access<K> > access;
	
	private final ObjectsCache<K, T> nextLevelCache;
	
	// private ObjectsCacheMemoryImpl() {
	public ObjectsCacheMemoryImpl(ObjectsCache<K, T> fileCache, int maxSize, CacheStrategy strategy) {
		this.nextLevelCache = fileCache;
		this.maxSize = maxSize;
		cache = new HashMap<>(maxSize);
		access = new HashMap<>(maxSize);
		this.strategy = strategy;
		cacheLock = new RWLock();
		accessLock = new RWLock();
	}
	
//	public static ObjectsCache getInstance() {
//		return instance;
//	}
	
	@Override
	public T get(K id) {
		T rv = null;
		cacheLock.getReadLock();
		if (cache.containsKey(id)) {
			rv = cache.get(id);
			accessLock.getWriteLock();
			Access<K> a = access.get(id);
			if (a == null) {
				a = new Access<K>(id);
				access.put(id, a);
			}
			a.time = System.nanoTime();
			a.count++;
			access.replace(id, a);
			accessLock.releaseLock();
			cacheLock.releaseLock();
		}
		else if (nextLevelCache != null) {
			cacheLock.releaseLock();
			rv = nextLevelCache.get(id);
			if (rv != null) {
				nextLevelCache.invalidate(id);
				this.put(id, rv);
			}
		}
		else {
			cacheLock.releaseLock();
		}
		return rv;
	}

	@Override
	public void put(K id, T obj) {
		cacheLock.getWriteLock();
		accessLock.getWriteLock();
		if (cache.containsKey(id)) {
			Access<K> a = access.get(id);
			if (a == null) {
				a = new Access<K>(id);
				access.put(id, a);
			}
			a.time = System.nanoTime();
			a.count++;
			access.replace(id, a);
			cache.replace(id, obj);
		} else {
			int free = maxSize - cache.size() - 1;
			if (free < 0) {
				free = -free;
				clearCache(free);
			}
			access.put(id, new Access<K>(id));
			cache.put(id, obj);
		}
		accessLock.releaseLock();
		cacheLock.releaseLock();
	}

	@Override
	public void invalidate(K id) {
		cacheLock.getWriteLock();
		accessLock.getWriteLock();
		access.remove(id);
		accessLock.releaseLock();
		cache.remove(id);
		cacheLock.releaseLock();
	}

	@Override
	public void clear() {
		cacheLock.getWriteLock();
		accessLock.getWriteLock();
		access.clear();
		accessLock.releaseLock();
		cache.clear();
		cacheLock.releaseLock();
	}

	@Override
	public int getCount() {
		cacheLock.getReadLock();
		int rv = cache.size();
		cacheLock.releaseLock();
		return rv;
	}

	@Override
	public void setStrategy(CacheStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public CacheStrategy getStrategy() {
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

	private void clearCache(int removeCount) {
		switch (strategy) {
			case LFU:
				clearUsingLFU(removeCount);
				break;
			case LRU:
				clearUsingLRU(removeCount);
				break;
			case MRU:
				clearUsingMRU(removeCount);
				break;
		}
	}

	private void removeElement(K id) {
		T obj = cache.get(id);
		cache.remove(id);
		access.remove(id);
		if (nextLevelCache != null) {
			nextLevelCache.put(id, obj);
		}
	}

	private void clearUsingLFU(int removeCount) {
		for (int i = removeCount; i > 0; i--) {
			Collection<Access<K> > ac = access.values();
			Access<K> lfu = Collections.min(ac, new Comparator<Access<K> >() {
			    @Override
			    public int compare(Access<K> first, Access<K> second) {
			        if (first.count > second.count)
			            return 1;
			        else if (first.count < second.count)
			            return -1;
			        return 0;
			    }
			});
			K id = lfu.id;
			removeElement(id);
		}
	}

	private void clearUsingLRU(int removeCount) {
		for (int i = removeCount; i > 0; i--) {
			Access<K> lru = Collections.min(access.values(), new Comparator<Access<K> >() {
			    @Override
			    public int compare(Access<K> first, Access<K> second) {
			        if (first.time > second.time)
			            return 1;
			        else if (first.time < second.time)
			            return -1;
			        return 0;
			    }
			});
			K id = lru.id;
			removeElement(id);
		}
	}

	private void clearUsingMRU(int removeCount) {
		for (int i = removeCount; i > 0; i--) {
			Access<K> mru = Collections.max(access.values(), new Comparator<Access<K> >() {
			    @Override
			    public int compare(Access<K> first, Access<K> second) {
			        if (first.time > second.time)
			            return 1;
			        else if (first.time < second.time)
			            return -1;
			        return 0;
			    }
			});
			K id = mru.id;
			removeElement(id);
		}
	}

	void printAllCached() {
		for (T c_obj : cache.values()) {
			System.out.println(c_obj.toString());
		}
	}
}
