package org.jhighfun.internal;

import org.junit.Test;

import java.lang.ref.SoftReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CacheObjectSpec {


    @Test(expected = NullPointerException.class)
    public void testConsForNullValue() {
        new CacheObject(null);
    }


    @Test
    public void testGetForObject() {
        Object object = new Object();
        CacheObject cacheObject = new CacheObject(object);
        assertTrue(cacheObject.get() == object);
    }

    @Test
    public void testEquals() {
        Object object = new Object();

        CacheObject cacheObject1 = new CacheObject(object);
        CacheObject cacheObject2 = new CacheObject(object);
        assertFalse(cacheObject1.equals(null));
        assertTrue(cacheObject1.equals(cacheObject2));
        cacheObject2 = null;
        assertFalse(cacheObject1.equals(cacheObject2));

        CacheObject cacheObject3 = new CacheObject("KEY");
        CacheObject cacheObject4 = new CacheObject("KEY");
        assertTrue(cacheObject3.equals(cacheObject4));

        CacheObject cacheObject5 = new CacheObject("KEY");
        CacheObject cacheObject6 = new CacheObject("VALUE");
        assertFalse(cacheObject5.equals(cacheObject6));

        CacheObject cacheObject7 = new CacheObject(new Object());
        CacheObject cacheObject8 = new CacheObject(new Object());
        assertFalse(cacheObject7.equals(cacheObject8));

        CacheObject cacheObject9 = new CacheObject(1);
        CacheObject cacheObject10 = new CacheObject(1);
        assertTrue(cacheObject9.equals(cacheObject10));

        CacheObject cacheObject11 = new CacheObject(10);
        CacheObject cacheObject12 = new CacheObject(100);
        assertFalse(cacheObject11.equals(cacheObject12));


    }

}