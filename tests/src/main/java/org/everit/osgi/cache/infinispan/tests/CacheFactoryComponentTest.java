package org.everit.osgi.cache.infinispan.tests;

/*
 * Copyright (c) 2011, Everit Kft.
 *
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

import java.util.HashMap;
import java.util.Map;

import javax.cache.Cache;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.cache.api.CacheFactory;
import org.everit.osgi.cache.infinispan.component.Constants;
import org.everit.osgi.dev.testrunner.TestDuringDevelopment;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;

/**
 * Unit test for CacheFactoryComponent.
 */
@Component(name = "CacheFactoryComponentTest", immediate = true)
@Service(value = CacheFactoryComponentTest.class)
@Properties({
        @Property(name = "eosgi.testId", value = "cachefactorycomponentTest"),
        @Property(name = "eosgi.testEngine", value = "junit4")
})
@TestDuringDevelopment
public class CacheFactoryComponentTest
{
    @Reference(bind = "bindConfigInit")
    private ConfigurationInitComponent configInit;

    @Reference(bind = "bindCacheFactory", target = "(clusterName=testcluster)")
    private CacheFactory cacheFactory;

    private BundleContext bundleContext;

    @Activate
    public void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void bindCacheFactory(final CacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public void bindConfigInit(final ConfigurationInitComponent configInit) {
        this.configInit = configInit;
    }

    /**
     * Testing the creation and usage of a Cache instance.
     */
    @Test
    public void cacheUsageTest()
    {
        Cache<String, Object> cache = cacheFactory.createCache(10, null);

        cache.put("key", "value");
        Assert.assertTrue("value".equals(cache.get("key")));

        cache.remove("key");
        Assert.assertTrue(cache.get("key") == null);

        Cache<String, Object> cache2 = cacheFactory.createCache(10, null);

        cache2.put("testkey", "testvalue");

        Assert.assertTrue("testvalue".equals(cache2.get("testkey")));
        Assert.assertTrue(cache.get("testkey") == null);

        cache2.close();
        cache.close();
    }

    @Test
    public void configParametersTest() {

        Map<String, Object> config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_DIST_ASYNC);
        config.put(Constants.PARAM_WAKEUPINTERVAL, 60000L);
        Cache<String, Object> cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, 155);
        config.put(Constants.PARAM_WAKEUPINTERVAL, "500");
        config.put(Constants.PARAM_MAXIDLE, "tizenot");
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_MAXIDLE, "tizenot");
        cache = cacheFactory.createCache(10, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_INVALIDATION_ASYNC);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_INVALIDATION_SYNC);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_LOCAL);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_REPL_ASYNC);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_REPL_SYNC);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, "value");
        cache = cacheFactory.createCache(10, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_DIST_SYNC);
        cache = cacheFactory.createCache(30, config);
        cache.close();

        config = new HashMap<String, Object>();
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_DIST_SYNC);
        config.put(Constants.PARAM_NUMOWNERS, "ot");
        cache = cacheFactory.createCache(30, config);
        cache.close();
    }

    /**
     * Testing the maxEntries parameter.
     */
    @Test
    public void maxEntriesTest()
    {
        Cache<String, Object> cache = cacheFactory.createCache(5, null);

        for (int i = 0; i <= 5; i++) {
            cache.put("key" + i, "value" + i);
        }

        Assert.assertTrue("value5".equals(cache.get("key5")));
        Assert.assertTrue(cache.get("key0") == null);

        cache.close();
    }

    /**
     * Testing the maxIdle option.
     */
    @Test
    public void maxIdleTest()
    {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(Constants.PARAM_MAXIDLE, 1000L);
        config.put(Constants.PARAM_NUMOWNERS, 3);
        config.put(Constants.PARAM_CACHEMODE, Constants.CACHEMODE_DIST_SYNC);

        Cache<String, Object> cache = cacheFactory.createCache(30, config);

        cache.put("testkey", "testvalue");

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(cache.get("testkey") == null);

        cache.close();
    }
}
