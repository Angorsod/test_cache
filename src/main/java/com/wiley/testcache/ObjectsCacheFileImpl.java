package com.wiley.testcache;

import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.wiley.testcache.berkeley.ObjectsCacheDatabase;
import com.wiley.testcache.berkeley.ObjectsCacheViews;

public class ObjectsCacheFileImpl<K, T> implements ObjectsCache<K, T> {

    private ObjectsCacheDatabase db;
    private ObjectsCacheViews views;
    private TransactionRunner runner;

	private ObjectsCache.CacheStrategy strategy;
	private int maxSize;
	
	public ObjectsCacheFileImpl(int maxSize, CacheStrategy strategy) {
		String homeDir = "./tmp";
		try {
			db = new ObjectsCacheDatabase(homeDir);
			views = new ObjectsCacheViews(db);
			runner = new TransactionRunner(db.getEnvironment());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.maxSize = maxSize;
		this.strategy = strategy;
	}

	public void close() {
		try {
			db.close();
		} catch (Exception e) {
			System.err.println("Exception during database close: ");
            e.printStackTrace();
		}
	}

	private class ReadWorker implements TransactionWorker
    {
		private final K key;
		private T value;
		private volatile boolean done;
		public ReadWorker(K key) {
			this.key = key;
			this.value = null;
			this.done = false;
		}
        @SuppressWarnings("unchecked")
		public void doWork() throws Exception {
        	value = (T)views.getCachedObjectMap().get(key);
        	done = true;
        }
        public T getValue() {
        	while (!done)
        		;
			return value;
		}
    }

	private class WriteWorker implements TransactionWorker
    {
		private final K key;
		private final T value;
		public WriteWorker(K key, T value) {
			this.key = key;
			this.value = value;
		}
        public void doWork() throws Exception {
        	views.getCachedObjectMap().put(key, value);
        }
    }

	private class RemoveWorker implements TransactionWorker
    {
		private final K key;
		public RemoveWorker(K key) {
			this.key = key;
		}
        public void doWork() throws Exception {
        	views.getCachedObjectMap().remove(key);
        }
    }

	@Override
	public T get(K id) {
		T rv = null;
		try {
			ReadWorker rw = new ReadWorker(id);
			runner.run(rw);
			rv = rw.getValue();
		} catch (Exception e) {}
		return rv;
	}

	@Override
	public void put(K id, T obj) {
		try {
			runner.run(new WriteWorker(id, obj));
		} catch (Exception e) {}
	}

	@Override
	public void invalidate(K id) {
		try {
			runner.run(new RemoveWorker(id));
		} catch (Exception e) {}
	}

	private class ClearWorker implements TransactionWorker
    {
        public void doWork() throws Exception {
        	views.getCachedObjectMap().clear();
        }
    }
	@Override
	public void clear() {
		try {
			runner.run(new ClearWorker());
		} catch (Exception e) {}
	}

	@Override
	public int getCount() {
		return views.getCachedObjectMap().size();
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
