package com.wiley.testcache.berkeley;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class ObjectsCacheDatabase {

	private Environment env;
    private static final String CLASS_CATALOG = "java_class_catalog";
    private StoredClassCatalog javaCatalog;

    private static final String OBJECTS_CACHE_STORE = "objects_cache_store";
    private Database objectsCacheDb;

	public ObjectsCacheDatabase(String homeDirectory) throws DatabaseException, FileNotFoundException {
		System.out.println("Opening environment in: " + homeDirectory);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);

        env = new Environment(new File(homeDirectory), envConfig);

        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setTransactional(true);
        dbConfig.setAllowCreate(true);

        objectsCacheDb = env.openDatabase(null, OBJECTS_CACHE_STORE, dbConfig);
        
        Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig);

        javaCatalog = new StoredClassCatalog(catalogDb);
	}

	public void close() throws DatabaseException {
		objectsCacheDb.close();
		javaCatalog.close();
		env.close();
	}
	
	public final Environment getEnvironment() {
        return env;
    }

    public final StoredClassCatalog getClassCatalog() {
        return javaCatalog;
    }
    
    public final Database getObjectsCacheDatabase() {
        return objectsCacheDb;
    }

}
