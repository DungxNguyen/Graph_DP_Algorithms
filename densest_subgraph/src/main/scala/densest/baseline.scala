package densest

import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.parallel.mutable.ParMap
import scala.collection.parallel.mutable.ParHashMap
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.File


//object baseline {
//  def main(args: Array[String]):Unit = {
//    val graph = utils.loadGraph("data/musae_chameleon_edges.txt")
//    
//    val graphPar = utils.cloneGraph(graph)
//
//    val start = System.currentTimeMillis()
//    val densest = baseline(graphPar)
//    val end = System.currentTimeMillis()
//    val denseSub = densest._2
//    
//    println(s"Baseline Densest subgraph: $densest")
//    println(s"Densest sub: $denseSub")
//    println("Densest size: " + denseSub.size)
//    println("Runtime: " + (end - start))
//    println("Density: " + densest._1)
//    
//    return 
//  }

  object baseline {
  def main(args: Array[String]): Unit = {
    val graph = utils.loadGraph("../data_graphs/" + args(0) + ".txt")

    val outputFile = "densest_baseline.txt"
    val subgraphFile = "densest_baseline_sub.txt"

    val net = args(0)
    val reps = 1

    val outputs : ArrayBuffer[Double] = ArrayBuffer()
    val runtimes : ArrayBuffer[Long] = ArrayBuffer()

    val pw = new PrintWriter(new FileOutputStream(new File(subgraphFile)), true)

    for (i <- 1 to reps) {
      val start = System.currentTimeMillis()
      val output = baseline(utils.cloneGraph(graph))

      pw.println(output)

      outputs += output._1
      val end = System.currentTimeMillis()
      runtimes += (end - start)
    }

    pw.close

    val mean = outputs.sum / outputs.size
    println("Density: " + mean)
    // println("Density: " + math.sqrt(outputs.map(p => math.pow(p - mean, 2)).sum / (outputs.size-1)))
    //println(s"Runtime: $runtimes")
    //
    val pw_summary = new PrintWriter(new FileOutputStream(new File(outputFile)), true)

    pw_summary.println(net, outputs)

    pw_summary.close
  }

  def baseline(graph: Map[Int, Set[Int]]): (Double, scala.collection.Set[Int], Int) = {
    val nNodes = graph size
    val densitySubGraphs: ArrayBuffer[Double] = ArrayBuffer()
    val nodes: Set[Int] = Set()
    graph.keySet.foreach(p => nodes.add(p))
    
    val removedNodes: ArrayBuffer[Int] = ArrayBuffer() 

    // while loop execution
    while (graph.size > 0) {
      // Calculate density of current subgraph
      val density = utils.rho(graph)
      densitySubGraphs += density
      //println(s"Desity: $density Subgraph: $subGraph")
      
      // Find the min degree node
      val minDegNode = graph.minBy(p => p._2.size)
      
      // Reconstruct the adjacent list:
      minDegNode._2.foreach(p => graph.getOrElse(p, Set()).remove(minDegNode._1))

      // Remove the min degree node
      graph.remove(minDegNode._1)
      removedNodes += minDegNode._1
      
    }

    val maxDensity = densitySubGraphs.max
    val maxDensityStep = densitySubGraphs.indexWhere(_ >= maxDensity)
    return (maxDensity, nodes -- removedNodes.slice(0, maxDensityStep + 1), nNodes) 
  }

}
