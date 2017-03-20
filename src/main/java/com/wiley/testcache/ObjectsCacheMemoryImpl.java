package com.wiley.testcache;

import java.util.HashMap;
import java.util.Map;

public class ObjectsCacheMemoryImpl implements ObjectsCache {

//	private final static ObjectsCacheMemoryImpl instance = new ObjectsCacheMemoryImpl();

	private final Map<Long, CachedObject> cache;
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
	private RWLock lock;
	
	// private ObjectsCacheMemoryImpl() {
	public ObjectsCacheMemoryImpl() {
		cache = new HashMap<>(100);
		strategy = ObjectsCache.CacheStrategy.LFU;
		maxSize = 10;
		lock = new RWLock();
	}
	
//	public static ObjectsCache getInstance() {
//		return instance;
//	}
	
	@Override
	public Object get(long id) {
		lock.getReadLock();
		Object rv = cache.get(id);
		lock.releaseLock();
		return rv;
	}

	@Override
	public void put(long id, Object obj) {
		lock.getWriteLock();
		cache.put(id, (CachedObject)obj);
		lock.releaseLock();
	}

	@Override
	public void invalidate(long id) {
		lock.getWriteLock();
		cache.remove(id);
		lock.releaseLock();
	}

	@Override
	public void clear() {
		lock.getWriteLock();
		cache.clear();
		lock.releaseLock();
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

}
