package com.anandnatrajan.plastichash.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  * A class to define algorithms that indicate WHAT to change in the
  * configuration history. Implementations are expected to replace the
  * configuration history in the server context.
  *
  * @author Anand Natrajan
  */
public abstract class WhatAlgorithm
{
	/**
	  * Invoke the algorithm to take a server context and modify it.
	  *
	  * @param sc the server context to modify.
	  */
	abstract public void invoke(ServerContext sc);

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

	// Provide a library of What algorithms for general use.

	static public class Squeeze extends WhatAlgorithm
	{
		public void invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			for (int i = 0; i < N.size() - 1; i++) // Using size on purpose.
			{
				if (N.get(i) == N.get(i+1))
				{
					// Squeeze out adjacent entries that are equal.
					N.remove(i+1);
					// Also, don't advance the loop counter yet.
					i--;
				}
			}
			sc.replaceAllEpochs(N);
		}
	}

	static public class Snap extends WhatAlgorithm
	{
		public void invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			final int size = N.size();
			if (size < 1)
				return; // Nothing to change.
			final int lastElem = N.get(size-1);
			N.clear();
			N.add(lastElem); // Push the last config in.
			sc.replaceAllEpochs(N);
		}
	}

	static public class Halve extends WhatAlgorithm
	{
		public void invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			final int size = N.size();
			if (size < 1)
				return; // Nothing to change.
			final int halfSize = (int) Math.round((double) size / 2);
			for (int i = 0; halfSize < N.size();) // Don't increment.
				N.remove(i);
			sc.replaceAllEpochs(N);
		}
	}

	static public class Spring extends WhatAlgorithm
	{
		public void invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			final int size = N.size();
			if (size < 1)
				return; // Nothing to change.
			// Find the first time the last entry occurred, and keep only
			// that much history.
			final int lastElem = N.get(size-1);
			int lastOccur = size - 1;
			for (int i = 0; i < lastOccur; i++)
			{
				if (N.get(i) == lastElem)
				{
					lastOccur = i;
					break;
				}
			}
			for (int i = lastOccur + 1; i < N.size();) // Using size on purpose.
				N.remove(i);
			sc.replaceAllEpochs(N);
		}
	}

	static public class Anneal extends WhatAlgorithm
	{
		public void invoke(ServerContext sc)
		{
			final List<Integer> N = sc.getAllEpochs();
			// Squeeze the config history, in each epoch.
			final int size = N.size();
			if (size < 1)
				return; // Nothing to change.
			// This array will become the annealed history, in reverse.
			List<Integer> newN = new ArrayList<>();
			// Squeeze conservatively, permitting only one change at a time.
			newN.add(N.get(size-1)); // Push the last config in.
			boolean isChanged = false; // false = not changed, true = changed.
			int i = size - 1;
			while (i-- > 0)
			{
				// Many cases to consider here. Let's do each carefully.
				if (N.get(i) == N.get(i + 1))
				{
					// Do nothing. Don't push into new N. True squeeze. Also,
					// not considered a change, since no allocations change.
				}
				else if (!isChanged && N.get(i) < N.get(i + 1))
				{
					// Growth phase. Adapt to the new config.
					newN.add(N.get(i) + 1);
					isChanged = true;
				}
				else if (!isChanged && N.get(i) > N.get(i + 1))
				{
					// Shrink phase. Adapt to the new config.
					newN.add(N.get(i) - 1);
					isChanged = true;
				}
				else
				{
					// Don't rock the boat. No change.
					newN.add(N.get(i));
				}
			}
			Collections.reverse(newN);
			sc.replaceAllEpochs(newN);
		}
	}
}
