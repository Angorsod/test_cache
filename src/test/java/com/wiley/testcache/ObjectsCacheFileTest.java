package com.wiley.testcache;

import junit.framework.TestCase;

public class ObjectsCacheFileTest extends TestCase {

//	private ObjectsCacheMemoryImpl<Long, CachedObject> testLvl1Cache;
//	private ObjectsCacheFileImpl<Long, CachedObject> testLvl2Cache;

	public ObjectsCacheFileTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
//		testLvl1Cache = new ObjectsCacheMemoryImpl<>();
//		testLvl2Cache = new ObjectsCacheFileImpl<>(testLvl1Cache);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGet() {
//		CachedObject testObj = new CachedObject(1, "Alex", 44);
//		assertNull(testLvl2Cache.get(1L));
//		testLvl2Cache.put(1L, testObj);
//		assertEquals(testLvl2Cache.get(1L), testObj);
//		testLvl2Cache.clear();
	}

//	public void testPut() {
//		fail("Not yet implemented");
//	}
//
//	public void testInvalidate() {
//		fail("Not yet implemented");
//	}
//
//	public void testClear() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetCount() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetStrategy() {
//		fail("Not yet implemented");
//	}
//
//	public void testSetMaxSize() {
//		fail("Not yet implemented");
//	}

}
