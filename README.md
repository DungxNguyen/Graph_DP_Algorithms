# Graph_DP_Algorithms
Collection of differentially private alogrithms on graph.

The definition of differential privacy relies heavily on the notion of neighboring. In the edge-privacy model, two neighbor graphs differ by exact 1 edge. In the node-privacy model, two neighbor graphs differ by 1 node and its adjacent edges. Hence, the node-privacy model is usually difficult to achieve and has worse privacy-utility trade-off.

In the edge-privacy model, edges are private while nodes can be publicly known. In the node-privacy model, both nodes and edges are kept private.

## Data Format
- We use the same network format with SNAP. All algorithms share the same data directory (data_graph). All graphs/networks are stored in txt files. There is a script to download and extract common networks for evalution from SNAP database.

## Graph statistics algorithms
Graph statistics algorithm's outputs are graph statistics. The statistics are either scalars (such as average degree, number of triangles, etc) or histograms/distributions (such as degree distributions). Differentially private graph statistics algorithms can be implemented in edge or node privacy model.

### Triangle Counting Algorithms
- shiva.py implements the node differentially private triangle counting algorithm described by "Analyzing graphs with node differential privacy".

## Graph structure algorithms
Graph structure algorithms output a sub-structure of the input graph. Therefore, they are only meaningful in the edge-privacy model since outputting a sub-structure exposes the private information (nodes of the input graph must be kept private).

### Densest Subgraph Algorithms
-  sequestial.scala implements the sequential variant of the DP densest subgraph algorithms in "Differentially Private Densest Subgraph Detection"
