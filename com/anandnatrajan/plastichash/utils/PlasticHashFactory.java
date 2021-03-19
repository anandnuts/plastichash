package com.anandnatrajan.plastichash.utils;

/**
 * Returns one instance of a load balancer. This instance is crafted with a
 * when and a what algorithm, and carries its own server context. That way,
 * we can use one instance per fleet we're load-balancing.
 *
 * @author Anand Natrajan
 */
public class PlasticHashFactory
{
	/**
	  * Creates a plastic hash instance with specified when and what
	  * algorithms.
	  */
	public PlasticHash createInstance
		(final WhenAlgorithm when, final WhatAlgorithm what)
	{
		return new PlasticHash().setWhen(when).setWhat(what);
	}

	/**
	  * Creates a plastic hash instance with default when and what
	  * algorithms, Stasis and Snap respectively.
	  */
	public PlasticHash createInstance()
	{
		return createInstance
			(new WhenAlgorithm.Stasis(), new WhatAlgorithm.Snap());
	}
}
