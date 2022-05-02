package densest

import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.File

object sequential {
  def main(args: Array[String]): Unit = {
    val graph = utils.loadGraph("../data_graphs/" + args(0) + ".txt")

    val outputFile = "densest_seq.txt"
    val subgraphFile = "densest_seq_sub.txt"

    val net = args(0)
    val epsilon = args(1).toDouble
    val delta = args(2).toDouble
    val reps = args(3).toInt
    
    val outputs : ArrayBuffer[Double] = ArrayBuffer()
    val runtimes : ArrayBuffer[Long] = ArrayBuffer()    

    val pw = new PrintWriter(new FileOutputStream(new File(subgraphFile)), true)

    for (i <- 1 to reps) {
      val start = System.currentTimeMillis()
      val output = privateDensest(utils.cloneGraph(graph), epsilon, delta)

      pw.println(output)

      outputs += output._1
      val end = System.currentTimeMillis()
      runtimes += (end - start) 
    }

    pw.close
    
    val mean = outputs.sum / outputs.size
    println("Density Mean: " + mean)
    println("Density Std: " + math.sqrt(outputs.map(p => math.pow(p - mean, 2)).sum / (outputs.size-1)))
    //println(s"Runtime: $runtimes")
    //
    val pw_summary = new PrintWriter(new FileOutputStream(new File(outputFile)), true)

    pw_summary.println(net, epsilon, delta, reps, outputs)

    pw_summary.close
  }
  
  def privateDensest(graph: Map[Int, Set[Int]], epsilon: Double, delta: Double): (Double, scala.collection.Set[Int], Int) = {
    val nNodes = graph size
    val densitySubGraphs: ArrayBuffer[Double] = ArrayBuffer()
    val subGraph = graph.clone
    val privacyRatio = 0.9
    //val sampleRatio = 0.1
    
    val epsilonP = epsilon / (2 * math.log(math.E/delta)) * privacyRatio

    val removedNodes: ArrayBuffer[Int] = ArrayBuffer() 
    // while loop execution
    while (subGraph.size > 0) {
      val density = utils.rho(subGraph)
      densitySubGraphs += density
      //println(density)
      
      // Randomize some node with probability proportional to -degree 
      val randomNode =  sampleNode(subGraph, epsilonP)
      
      // Reconstruct the adjacent list:
      randomNode._2.foreach(p => subGraph.getOrElse(p, Set()).remove(randomNode._1))

      // Remove the min degree node
      subGraph.remove(randomNode._1)
      removedNodes += randomNode._1
       
    }

    val sampledSub = utils.sampleRho(densitySubGraphs, epsilon * (1-privacyRatio))
    // sample 10%
    //val sampledSub = utils.sampleRho(densitySubGraphs.filter(p => Random.nextDouble < sampleRatio), epsilon * (1-privacyRatio)/sampleRatio)
    // println(s"Density subgraphs $densitySubGraphs")
    return (sampledSub._1, graph.keySet -- removedNodes.slice(0, sampledSub._2), nNodes)
  }


  // https://stackoverflow.com/questions/24869304/scala-how-can-i-generate-numbers-according-to-an-expected-distribution
  final def sampleNode(dist: Map[Int, Set[Int]], epsilon: Double): (Int, Set[Int]) = {
    val p = Random.nextDouble * dist.map(p => math.exp(-epsilon * p._2.size)).sum
    val iter = dist.iterator
    var accumulative = 0.0
    while (iter.hasNext) {
      val (node, adjList) = iter.next
      accumulative += math.exp(-epsilon * adjList.size)
      if (accumulative >= p)
        return (node, adjList)
    }
    sys.error(f"Distribution error") // needed so it will compile
  }

}
