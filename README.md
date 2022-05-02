# Graph_DP_Algorithms
Collection of differentially private alogrithms on graph.

The definition of differential privacy relies heavily on the notion of neighboring. In the edge-privacy model, two neighbor graphs differ by exact 1 edge. In the node-privacy model, two neighbor graphs differ by 1 node and its adjacent edges. Hence, the node-privacy model is usually difficult to achieve and has worse privacy-utility trade-off.

In the edge-privacy model, edges are private while nodes can be publicly known. In the node-privacy model, both nodes and edges are kept private.

## Data Format
- We use the same network format with SNAP. All algorithms share the same data directory (data_graph). All graphs/networks are stored in txt files. There is a script to download and extract common networks for evalution from SNAP database.

## Graph statistics algorithms
Graph statistics algorithm's outputs are graph statistics. The statistics are either scalars (such as average degree, number of triangles, etc) or histograms/distributions (such as degree distributions). Differentially private graph statistics algorithms can be implemented in edge or node privacy model.

### Triangle Counting Algorithms (triangle_counting)
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

### Densest Subgraph Algorithms (densest_subgraph)
-  sequestial.scala implements the sequential variant of the DP densest subgraph algorithms in "Differentially Private Densest Subgraph Detection"
