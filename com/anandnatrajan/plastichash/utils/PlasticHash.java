package com.anandnatrajan.plastichash.utils;

import java.util.List;

/**
 * A plastic hash instance encapsulates one each of a configuration
 * history, a when algorithm and a what algorithm. Put together the
 * instance remembers as much of the configuration history as needed, and
 * can act on that configuration history as server counts change.
 *
 * @author Anand Natrajan
 */
public class PlasticHash
{
	private ServerContext serverContext;
	private WhenAlgorithm when;
	private WhatAlgorithm what;

	/**
	 * Local constructor, forcing instances to be created using a factory.
	 */
	PlasticHash()
	{
		this.serverContext = new ServerContext();
	}

	// And no public getters/setters for private variables, on purpose.

	/**
	 * Get the when algorithm.
	 *
	 * @return the when algorithm.
	 */
	protected WhenAlgorithm getWhen()
	{
		return when;
	}

	/**
	 * Set the when algorithm.
	 *
	 * @param the when algorithm.
	 * @return the current instance, just so we can chain calls.
	 */
	protected PlasticHash setWhen(WhenAlgorithm when)
	{
		this.when = when;
		return this;
	}

	/**
	 * Get the what algorithm.
	 *
	 * @return the what algorithm.
	 */
	protected WhatAlgorithm getWhat()
	{
		return what;
	}

	/**
	 * Set the what algorithm.
	 *
	 * @param what the what algorithm.
	 * @return the current instance, just so we can chain calls.
	 */
	protected PlasticHash setWhat(WhatAlgorithm what)
	{
		this.what = what;
		return this;
	}

	/**
	 * Get the server context.
	 *
	 * @return the server context.
	 */
	ServerContext getServerContext()
	{
		return serverContext;
	}

	/**
	 * Add another entry to the configuration history, invoking the
	 * when/what algorithms as needed.
	 *
	 * @param n the number of servers to keep in the new epoch.
	 * @return the current instance, just so we can chain calls.
	 */
	public PlasticHash addEpoch(final int n)
	{
		serverContext.addEpoch(n);
		// Run whatever configuration history cleanup we want.
		if (when.invoke(serverContext))
			what.invoke(serverContext);
		return this;
	}

	/**
	 * Get the server allocation for one request.
	 *
	 * @param id the id of the request.
	 * @return the number of the server, zero-based.
	 */
	public int getServer(final long id)
	{
		final List<Integer> N = serverContext.getAllEpochs();
		// The list above is a copy, so we can read it in peace without
		// worrying about becoming inconsistent. Stale perhaps, but not
		// inconsistent. Trying to always use the freshest configuration
		// over-complicates things, because every access would have to be
		// synchronised. That would slow down this algorithm, for barely
		// any difference in how the algorithm operates.
		int No = N.get(0);
		int So = (int) (id % No);
		final int size = N.size();
		for (int i = 1; i < size; i++)
		{
			int Nn = N.get(i);
			// If the server config array reaches zeros, it means we're done
			// with the config changes.
			if (Nn <= 0)
				break;
			int Sn = (int) (id % Nn);
			if ((Nn > No && Sn >= No) || (Nn < No && So >= Nn))
			{
				No = Nn;
				So = Sn;
			}
		}
		// There's a small danger here that by the time we get here, the
		// server context has changed, and the selected server number is
		// larger than the fleet size. We could, in theory run through the
		// algorithm again... and again... and again until we get a server
		// within the fleet. Since changing the fleet size is expected to
		// be a relatively rare activity, at least as compared to the
		// influx of requests, here, we have chosen to direct all such
		// orphans to server 0, which will always be around.
		return (So < serverContext.getLastEpoch() ? So : 0);
	}

	/**
	  * Human-friendly string representation, NOT to be used for any
	  * manipulation of configuration history.
	  *
	  * @return string version of this instance.
	  */
	public String toString()
	{
		return "When=" + when + " What=" + what + " Srv.Cont.=" + serverContext;
	}
}
