package com.wiley.testcache.berkeley;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredMap;
import com.wiley.testcache.CachedObject;

public class ObjectsCacheViews {

	private StoredMap objectsCacheMap;

    public ObjectsCacheViews(ObjectsCacheDatabase db) {
        ClassCatalog catalog = db.getClassCatalog();
        EntryBinding cachedObjKeyBinding =
            new SerialBinding(catalog, Long.class);
        EntryBinding cachedObjDataBinding =
            new SerialBinding(catalog, CachedObject.class);
        
        objectsCacheMap = new StoredMap(db.getObjectsCacheDatabase(),
        	cachedObjKeyBinding, cachedObjDataBinding, /* writeAllowed */ true);
	}

    public final StoredMap getCachedObjectMap() {
        return objectsCacheMap;
    }
    
    public final StoredEntrySet getCachedObjectEntrySet() {
        return (StoredEntrySet)objectsCacheMap.entrySet();
    }
    
}
