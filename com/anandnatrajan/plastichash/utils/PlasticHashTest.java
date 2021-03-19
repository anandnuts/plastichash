package com.anandnatrajan.plastichash.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
  * Test class to show how to use the plastic hash algorithm and also run a
  * bunch of test cases to verify functionality.
  *
  * @author Anand Natrajan
  */
public class PlasticHashTest
{
	private final PlasticHashFactory phf;
	private final boolean fastFail;

	public PlasticHashTest(final boolean fastFail)
	{
		this.fastFail = fastFail;
		this.phf = new PlasticHashFactory();
	}

	public PlasticHashTest()
	{
		this(true);
	}

	protected void setUp()
	{
        log("\n---------- BEGIN "
			+ new Throwable().getStackTrace()[1].getMethodName());
	}

	protected void tearDown(final boolean result)
	{
        log("---------- END " + (result ? "PASSED " : "FAILED ")
			+ new Throwable().getStackTrace()[1].getMethodName());
	}

	protected static void log(final String str)
	{
		System.out.println(str);
	}

	protected boolean assertEquals(final String message,
		final int expected, final int actual)
	{
		log("\t" + message + ", expected " + expected + ", got " + actual);
		try
		{
			assert expected == actual;
			return true;
		}
		catch (AssertionError ae)
		{
			if (fastFail)
				throw ae;
		}
		return false;
	}

	private static long hash(int id)
	{
		// A toy hashing function to illustrate how it could work.
		// Can do whatever hashing we want to do to the id.
		return (long) id;
	}

	// Run all requests through whatever the configuration is.
	private int runRequests(final PlasticHash ph, final int numRequests)
	{
		List<Integer> serverAlloc = new ArrayList<>();
		for (int i = 0; i < numRequests; i++)
			serverAlloc.add(ph.getServer(hash(i)));
		log("\tReq: " + serverAlloc);
		return new HashSet<Integer>(serverAlloc).size();
	}

	private boolean addEpochs(final PlasticHash ph, int... epochs)
	{
		final int numRequests = 20;
		boolean result = true;
		for (int servers : epochs)
		{
			log("before: " + ph + " + " + servers);
			ph.addEpoch(servers);
			log(" after: " + ph);
			final int actual = runRequests(ph, numRequests);
			result &= assertEquals("num servers", servers, actual);
		}
		return result;
	}

	private boolean checkEpochs(final PlasticHash ph,
		final int numEpochs, final int lastEpoch)
	{
		final ServerContext sc = ph.getServerContext();
		return assertEquals("num epochs", numEpochs, sc.getNumEpochs())
			&& assertEquals("last epoch", lastEpoch, sc.getLastEpoch());
	}

	public PlasticHashTest testBasicFunctionality()
	{
		setUp();
		final PlasticHash ph = phf.createInstance();
		final boolean result = true
			&& addEpochs(ph, 5)
			&& addEpochs(ph, 7)
			&& addEpochs(ph, 4)
			&& addEpochs(ph, 2)
			&& addEpochs(ph, 2)
			&& addEpochs(ph, 6)
			&& addEpochs(ph, 3)
			&& addEpochs(ph, 9)
			&& addEpochs(ph, 8)
			&& addEpochs(ph, 7)
			&& addEpochs(ph, 7)
			&& addEpochs(ph, 7)
			&& addEpochs(ph, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testStasisSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance();
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 2, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testStasisSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Stasis(), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testStasisHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Stasis(), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 4, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testStasisSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Stasis(), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 3, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testStasisAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Stasis(), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 9, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testNeverSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Never(), new WhatAlgorithm.Snap());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 13, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testNeverSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Never(), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 13, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testNeverHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Never(), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 13, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testNeverSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Never(), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 13, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testNeverAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Never(), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 13, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testAlwaysSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Always(), new WhatAlgorithm.Snap());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testAlwaysSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Always(), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testAlwaysHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Always(), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testAlwaysSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Always(), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testAlwaysAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Always(), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 5, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testPeriodicSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Periodic(5), new WhatAlgorithm.Snap());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testPeriodicSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Periodic(5), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testPeriodicHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Periodic(5), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 3, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testPeriodicSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Periodic(5), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 4, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testPeriodicAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.Periodic(5), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 8, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testOnDemandSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.OnDemand(), new WhatAlgorithm.Snap());
		boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7)
			&& checkEpochs(ph, 8, 7);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testOnDemandSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.OnDemand(), new WhatAlgorithm.Squeeze());
		boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7)
			&& checkEpochs(ph, 11, 7);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testOnDemandHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.OnDemand(), new WhatAlgorithm.Halve());
		boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7)
			&& checkEpochs(ph, 10, 7);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 5)
			&& checkEpochs(ph, 6, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testOnDemandSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.OnDemand(), new WhatAlgorithm.Spring());
		boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7)
			&& checkEpochs(ph, 11, 7);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 5)
			&& checkEpochs(ph, 1, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testOnDemandAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.OnDemand(), new WhatAlgorithm.Anneal());
		boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7)
			&& checkEpochs(ph, 11, 7);
		((WhenAlgorithm.OnDemand) ph.getWhen()).setGo(true);
		result &= true
			&& addEpochs(ph, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testLowServerCountSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.LowServerCount(5), new WhatAlgorithm.Snap());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 1, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 7, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testLowServerCountSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.LowServerCount(5), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 12, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testLowServerCountHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.LowServerCount(5), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 2, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 2, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 8, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testLowServerCountSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.LowServerCount(5), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 12, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testLowServerCountAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.LowServerCount(5), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 11, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testHighServerCountSnap()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.HighServerCount(5), new WhatAlgorithm.Snap());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 2, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testHighServerCountSqueeze()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.HighServerCount(5), new WhatAlgorithm.Squeeze());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 10, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testHighServerCountHalve()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.HighServerCount(5), new WhatAlgorithm.Halve());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 3, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 3, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testHighServerCountSpring()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.HighServerCount(5), new WhatAlgorithm.Spring());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 3, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testHighServerCountAnneal()
	{
		setUp();
		final PlasticHash ph = phf.createInstance
			(new WhenAlgorithm.HighServerCount(5), new WhatAlgorithm.Anneal());
		final boolean result = true
			&& addEpochs(ph, 5, 7, 4, 2)
			&& checkEpochs(ph, 4, 2)
			&& addEpochs(ph, 2)
			&& checkEpochs(ph, 5, 2)
			&& addEpochs(ph, 6, 3, 9, 8, 7, 7, 7, 5)
			&& checkEpochs(ph, 8, 5);
		tearDown(result);
		return this;
	}

	public PlasticHashTest testWhatWhenCombinations()
	{
		return this
			.testStasisSnap()
			.testStasisSqueeze()
			.testStasisHalve()
			.testStasisSpring()
			.testStasisAnneal()
			.testNeverSnap()
			.testNeverSqueeze()
			.testNeverHalve()
			.testNeverSpring()
			.testNeverAnneal()
			.testAlwaysSnap()
			.testAlwaysSqueeze()
			.testAlwaysHalve()
			.testAlwaysSpring()
			.testAlwaysAnneal()
			.testPeriodicSnap()
			.testPeriodicSqueeze()
			.testPeriodicHalve()
			.testPeriodicSpring()
			.testPeriodicAnneal()
			.testOnDemandSnap()
			.testOnDemandSqueeze()
			.testOnDemandHalve()
			.testOnDemandSpring()
			.testOnDemandAnneal()
			.testLowServerCountSnap()
			.testLowServerCountSqueeze()
			.testLowServerCountHalve()
			.testLowServerCountSpring()
			.testLowServerCountAnneal()
			.testHighServerCountSnap()
			.testHighServerCountSqueeze()
			.testHighServerCountHalve()
			.testHighServerCountSpring()
			.testHighServerCountAnneal();
	}

	public static void main(String... args)
	{
		new PlasticHashTest() // fast-fail is true by default.
			.testBasicFunctionality()
			.testWhatWhenCombinations();
	}
}
