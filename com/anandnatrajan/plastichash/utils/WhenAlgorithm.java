package com.anandnatrajan.plastichash.utils;

import java.util.List;

/**
  * A class to define algorithms that indicate WHEN to change the
  * configuration history. Implementations are expected to return just
  * true/false.
  *
  * @author Anand Natrajan
  */
public abstract class WhenAlgorithm
{
	/**
	  * Invoke the algorithm to take a server context and return an
	  * indicaation of whether it is time to change it.
	  *
	  * @param sc the original server context.
	  * @return true or false, if the server context must be changed.
	  */
	abstract public boolean invoke(ServerContext sc);

	/**
	  * Get a unique ID for an instance of the algorithm. Generally, the ID
	  * should be unique within one JVM, although there is no mandate to do
	  * so. Different factories could implement different policies.
	  *
	  * @return a unique ID for an instance of the algorithm.
	  */
	// abstract public String getId();

	public String toString()
	{
		return this.getClass().getSimpleName();
	}

	// Provide a library of When algorithms for general use.

	/**
	  * Never a good time to modify configuration history.
	  */
	static public class Never extends WhenAlgorithm
	{
		public boolean invoke(ServerContext sc)
		{
			return false; // Yep, that's never.
		}
	}

	/**
	  * Always a good time to modify configuration history.
	  */
	static public class Always extends WhenAlgorithm
	{
		public boolean invoke(ServerContext sc)
		{
			return true; // Yep, that's always.
		}
	}

	/**
	  * Modify the configuration history after every kth epoch.
	  */
	static public class Periodic extends WhenAlgorithm
	{
		final private int k;

		public Periodic(final int k)
		{
			this.k = k;
		}

		public boolean invoke(ServerContext sc)
		{
			return (sc.getNumEpochs() % k == 0); // Every kth time.
		}

		public String toString()
		{
			return super.toString() + "(" + k + ")";
		}
	}

	/**
	  * Modify the configuration history on demand.
	  */
	static public class OnDemand extends WhenAlgorithm
	{
		private boolean go;

		public void setGo(final boolean go)
		{
			this.go = go;
		}

		public boolean isGo()
		{
			return go;
		}

		public boolean invoke(ServerContext sc)
		{
			final boolean wasGo = go;
			go = false;
			return wasGo; // Pretty capricious.
		}

		public String toString()
		{
			return super.toString() + "(" + go + ")";
		}
	}

	static public class Stasis extends WhenAlgorithm
	{
		public boolean invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			final int size = N.size();
			// Check last two entries.
			return (size > 1 && N.get(size - 1) == N.get(size - 2));
		}
	}

	/**
	  * Modify the configuration history when server count goes below
	  * threshold.
	  */
	static public class LowServerCount extends WhenAlgorithm
	{
		private final int threshold;

		public LowServerCount(final int threshold)
		{
			this.threshold = threshold;
		}

		public boolean invoke(ServerContext sc)
		{
			return (sc.getLastEpoch() < threshold);
		}

		public String toString()
		{
			return super.toString() + "(" + threshold + ")";
		}
	}

	/**
	  * Modify the configuration history when server count goes above
	  * threshold.
	  */
	static public class HighServerCount extends WhenAlgorithm
	{
		private final int threshold;

		public HighServerCount(final int threshold)
		{
			this.threshold = threshold;
		}

		public boolean invoke(ServerContext sc)
		{
			return (sc.getLastEpoch() > threshold);
		}

		public String toString()
		{
			return super.toString() + "(" + threshold + ")";
		}
	}
}
