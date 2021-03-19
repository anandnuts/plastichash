package com.anandnatrajan.plastichash.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The server context class stores the server configuration history for a
 * system. The class is intended to be thread-safe for changes made to the
 * configuration history.
 *
 * @author Anand Natrajan
 */
public class ServerContext
{
	// Store the historical configuration as a list of server counts.
	final private List<Integer> N = new ArrayList<>();

	/**
	 * Get the last epoch entry, or -1 if no epoch history exists.
	 *
	 * @return the number of servers in the latest epoch.
	 */
	public int getLastEpoch()
	{
		synchronized (N)
		{
			final int size = N.size();
			return (size > 0) ? N.get(size-1) : -1;
		}
	}

	/**
	 * Get the count of epochs in the configuration history.
	 *
	 * @return the number of epochs in the history.
	 */
	public int getNumEpochs()
	{
		return N.size();
	}

	/**
	 * Get a copy of all of the epochs, or empty if no epoch history
	 * exists. The method returns a copy so as to be thread-safe.
	 *
	 * @return the epochs.
	 */
	public List<Integer> getAllEpochs()
	{
		synchronized (N)
		{
			return new ArrayList<>(N);
		}
	}

	/**
	 * Add another entry to the configuration history, invoking the
	 * when/what algorithms as needed.
	 *
	 * @param n the number of servers to keep in the new epoch.
	 * @return the current instance, just so we can chain calls.
	 */
	public ServerContext addEpoch(final int n)
	{
		synchronized (N)
		{
			N.add(n);
		}
		return this;
	}

	/**
	 * Replace the entire configuration history with a new one. If the new
	 * configuration history is empty, it effectively wipes out the
	 * history, leaving no epochs in place. That's a bad idea because it
	 * means no new requests can be processed.
	 *
	 * @param newN the new configuration history as a list of epochs.
	 * @return the current instance, just so we can chain calls.
	 */
	public ServerContext replaceAllEpochs(final List<Integer> newN)
	{
		synchronized (N)
		{
			N.clear();
			N.addAll(newN);
		}
		return this;
	}

	/**
	  * Human-friendly string representation, NOT to be used for any
	  * manipulation of configuration history.
	  *
	  * @return string version of this instance.
	  */
	public String toString()
	{
		return N.toString();
	}
}
