# plastichash
Plastic Hashing for Even, Stable, Fast Load Balancing

Plastic hash is an approach for assigning clients to servers when the connections between them may be stateful. With stateful connections, it is beneficial to assign a client to the same server repeatedly. However, as server counts change, redistributing clients becomes necessary to maintain an even load over the surviving servers. Redistributing clients, while still trying to preserve client-server connections as much as possible is hard. Plastic hash is one approach to solving the problem.

Details of the approach can be found in this paper: http://www.anandnatrajan.com/papers/CACM20b.pdf

This open-source project implements the plastic hash algorithm. It also implements a family of when/what algorithms that can be used to configure the plastic hash algorithm. The project includes unit tests as well.

The project can be compiled using:
	`javac com/anandnatrajan/plastichash/utils/*.java`
or
	`make compile`

To run the unit tests, run:
	`java -enableassertions com.anandnatrajan.plastichash.utils.PlasticHashTest`
or
	`make test`

The project is made open-source under the MIT licence terms. The code should be compiled within load-balancer applications as desired.
