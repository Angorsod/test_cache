package com.wiley.testcache;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ObjectsCacheMemoryImpl implements ObjectsCache {

//	private final static ObjectsCacheMemoryImpl instance = new ObjectsCacheMemoryImpl();

	private Map<Long, CachedObject> cache;
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
	private class Access{
		long id;
		long time;
		int count;
		public Access(long id) {
			// time = System.currentTimeMillis();
			count = 0;
			this.id = id;
		}
	}
	private Map<Long, Access> access;
	
	
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
	public Object get(long id) {
		cacheLock.getReadLock();
		accessLock.getWriteLock();
		Access a = access.get(id);
		if (a == null) {
			a = new Access(id);
			access.put(id, a);
		}
		a.time = System.currentTimeMillis();
		a.count++;
		access.replace(id, a);
		accessLock.releaseLock();
		Object rv = cache.get(id);
		cacheLock.releaseLock();
		return rv;
	}

	@Override
	public void put(long id, Object obj) {
		cacheLock.getWriteLock();
		accessLock.getWriteLock();
		int free = maxSize - cache.size() - 1;
		if (free < 0) {
			free = -free;
			clearCache(free);
		}
		accessLock.releaseLock();
		cache.put(id, (CachedObject)obj);
		cacheLock.releaseLock();
	}

	@Override
	public void invalidate(long id) {
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
			Collection<Access> ac = access.values();
			Access lfu = Collections.min(ac, new Comparator<Access>() {
			    @Override
			    public int compare(Access first, Access second) {
			        if (first.count > second.count)
			            return 1;
			        else if (first.count < second.count)
			            return -1;
			        return 0;
			    }
			});
			long id = lfu.id;
			cache.remove(id);
			access.remove(id);
		}
	}

	private void clearUsingLRU(int removeCount) {
		for (int i = removeCount; i > 0; i--) {
			Access lru = Collections.min(access.values(), new Comparator<Access>() {
			    @Override
			    public int compare(Access first, Access second) {
			        if (first.time > second.time)
			            return 1;
			        else if (first.time < second.time)
			            return -1;
			        return 0;
			    }
			});
			long id = lru.id;
			cache.remove(id);
			access.remove(id);
		}
	}

	private void clearUsingMRU(int removeCount) {
		for (int i = removeCount; i > 0; i--) {
			Access mru = Collections.max(access.values(), new Comparator<Access>() {
			    @Override
			    public int compare(Access first, Access second) {
			        if (first.time > second.time)
			            return 1;
			        else if (first.time < second.time)
			            return -1;
			        return 0;
			    }
			});
			long id = mru.id;
			cache.remove(id);
			access.remove(id);
		}
	}

}
