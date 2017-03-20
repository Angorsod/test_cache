package com.wiley.testcache;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ObjectsCacheMemoryImpl<K, T> implements ObjectsCache<K, T> {

//	private final static ObjectsCacheMemoryImpl instance = new ObjectsCacheMemoryImpl();

	private Map<K, T> cache;
	private ObjectsCache.CacheStrategy strategy;
	private int maxSize;
	
	/**
	 * Read/write lock
	 */
	private class RWLock {
		private int givenLocks;
		private int waitingWriters;
		// private int waitingReaders;
		private Object mutex;
		
		public RWLock() {
			mutex = new Object();
			givenLocks = 0;
			waitingWriters = 0;
			// waitingReaders = 0;
		}
		
		public void getReadLock() {
			synchronized (mutex) {
				while ((givenLocks == -1) || (waitingWriters != 0)) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
						return;
					}
				}
				givenLocks++;
			}
		}
		
		public void getWriteLock() {
			synchronized (mutex) {
				waitingWriters++;
				while (givenLocks != 0) {
					try {
						mutex.wait();
					} catch (InterruptedException e) {
						System.err.println(e.getMessage());
						return;
					}
				}
				waitingWriters--;
				givenLocks = -1;
			}
		}
		
		public void releaseLock() {
			synchronized (mutex) {
				if (givenLocks == 0)
					return;
				if (givenLocks == -1)
					givenLocks = 0;
				else
					givenLocks--;
				mutex.notifyAll();
			}
		}
	}
	private RWLock cacheLock;
	private RWLock accessLock;
	
	/**
	 * Access map is used to an implement different cache strategies
	 */
	private class Access<I> {
		I id;
		long time;
		int count;
		public Access(I id) {
			// time = System.currentTimeMillis();
			count = 0;
			this.id = id;
		}
	}
	private Map<K, Access<K> > access;
	
	
	// private ObjectsCacheMemoryImpl() {
	public ObjectsCacheMemoryImpl() {
		maxSize = 10;
		cache = new HashMap<>(maxSize);
		access = new HashMap<>(maxSize);
		strategy = ObjectsCache.CacheStrategy.LFU;
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
			a.time = System.currentTimeMillis();
			a.count++;
			access.replace(id, a);
			accessLock.releaseLock();
		}
		cacheLock.releaseLock();
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
			a.time = System.currentTimeMillis();
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
			cache.remove(id);
			access.remove(id);
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
			cache.remove(id);
			access.remove(id);
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
			cache.remove(id);
			access.remove(id);
		}
	}

	void printAllCached() {
		for (T c_obj : cache.values()) {
			System.out.println(c_obj.toString());
		}
	}
}
