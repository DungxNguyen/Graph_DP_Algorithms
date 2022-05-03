# Graph_DP_Algorithms
Collection of differentially private alogrithms on graph.

The definition of differential privacy relies heavily on the notion of neighboring. In the edge-privacy model, two neighbor graphs differ by exact 1 edge. In the node-privacy model, two neighbor graphs differ by 1 node and its adjacent edges. Hence, the node-privacy model is usually difficult to achieve and has worse privacy-utility trade-off.

In the edge-privacy model, edges are private while nodes can be publicly known. In the node-privacy model, both nodes and edges are kept private.

## Data Format
- We use the same network format with SNAP. All algorithms share the same data directory (data_graph). All graphs/networks are stored in txt files. There is a script to download and extract common networks for evalution from SNAP database.

## Graph statistics algorithms
Graph statistics algorithm's outputs are graph statistics. The statistics are either scalars (such as average degree, number of triangles, etc) or histograms/distributions (such as degree distributions). Differentially private graph statistics algorithms can be implemented in edge or node privacy model.

### Triangle Counting Algorithms [triangle_counting](https://github.com/DungxNguyen/Graph_DP_Algorithms/tree/master/triangle_counting)
- shiva.py implements the node differentially private triangle counting algorithm described by "Analyzing graphs with node differential privacy".

Algorithms in this sub-section are implemented in Python 3. They requires the following libraries (can be installed via pip/conda):
- scipy
- numpy
- networkx
- gurobipy (the system must install gurobi in order to use gurobi for optimization)

All algorithms have similar command line arguments to define their settings: python algorithm_name.py network_name epsilon delta repeat [params] solver
- algorithm_name.py: for example, shiva.py, blocki_edge.py, etc
- network_name: the name (without .txt) of the network file, e.g., ca-GrQc
- epsilon: a positive real number that indicates the privacy loss, e.g., 0.01, 0.1, 1, etc
- delta: a small non-negative number that indicates the castatrophic probability that privacy doesn't hold, which is 0 (pure-DP) or << 1/n in which n is the database size
- repeat: the number of instances to run
- [params]: some algorithms requires extra parameters. E.g., shiva.py requires the maximum degree of a node in the graph (D). 
- solver: gurobi or scipy

For example to run Shiva et. al.'s algorithm with privacy loss of 1, with maximum degree D = 50 and repeats 10 times using scipy optimizer: python shiva.py ca-GrQc 1.0 0 10 [50] scipy

Outputs are written to respective .csv file. Each line contains the specific settings of each run of the algorithm (epsilon, delta, params, etc), the true count of triangles and the DP-output count of triangle. The spicific fields are: "times, privacy_model, alg_name, network, true_count, epsilon, delta, params, dp-count". For example this is one line of the result from the above setting:

1651526841.7423203,node_privacy,shiva_alg,ca-GrQc,48260,1.0,0.0,[50],44498.92723850597

Note that the file will contains 10 lines like this because we set it up for 10 repeats.



## Graph structure algorithms
Graph structure algorithms output a sub-structure of the input graph. Therefore, they are only meaningful in the edge-privacy model since outputting a sub-structure exposes the private information (nodes of the input graph must be kept private).

### Densest Subgraph Algorithms [densest_subgraph](https://github.com/DungxNguyen/Graph_DP_Algorithms/tree/master/densest_subgraph)
-  baseline.scala implements the Charikar's greedy algorithm for densest subgraph detection, described in "Greedy approximation algorithms for finding dense components in a graph"
-  sequestial.scala implements the sequential variant of the DP densest subgraph algorithms in "Differentially Private Densest Subgraph Detection"

Algorithms in this sub-section are implemented using Scala 2. The scala environment and project's dependencies can be automatically retrieved by [sbt](https://www.scala-sbt.org/) tool. In the first run, go to the directory (densest_subgraph) and run "sbt compile". Sbt will create an environment with appropriate libraries and compile all source files.

To run the differentially private densest subgraph algorithm, run "sbt "runMain densest.algorithm_name network epsilon delta repeat". The semantics of the parameters are the same with the above sub-section. For example, to run the sequential algorithm with epsilon = 0.1, delta = 0.0000001, and 10 times:
"sbt "runMain densest.sequential 0.1 0.00000001 10"".

Because the output of densest subgraph detection algorithm is a subgraph, the output of the algorithms will consist of 2 files: densest_\*.txt and densest_\*_sub.txt

The first file looks like this:

- (ca-GrQc,0.1,1.0E-6,10,ArrayBuffer(2.262771168649405, 1.3027027027027027, 2.346774193548387, 1.406545592130155, 1.5419580419580419, 0.16104868913857678, 0.058823529411764705, 2.362597114317425, 1.702884300229433, 2.6731640086631225))

, which indicates the network, epsilon, delta, repeats and a list of the densities of output subgraphs (in this case, there are 10 outputs of 10 repeats).

The second file contains the ids of nodes output by each run. For example, a line of the second file:
- (2.6731640086631225,Set(10822, 5385, 5659, 19204, 24939, 12928, 1322, 2630, 23855, 24878, 3873, 2452, 21321, 15170, 16261, 18279, 16340, 1879,...)

, where the first value is the density of the subgraph, followed by a list of node's ids of the subgraph.



