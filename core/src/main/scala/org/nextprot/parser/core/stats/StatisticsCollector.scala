package org.nextprot.parser.core.stats

import java.util.TreeMap
import scala.collection.concurrent.TrieMap

/**
 * Stats is a singleton
 */
object Stats extends StatisticsCollector;

class StatisticsCollector {

  val stats: TrieMap[String, TrieMap[String, (String, Integer)]] = new TrieMap()

  def ++(metric: String, label: String) = {
    val metricValues = stats.getOrElse(metric, new TrieMap());
    val labelValue: (String, Integer) = metricValues.getOrElse(label, (label, 0));
    val newValue = labelValue._2 + 1;
    metricValues.put(label, (label, newValue));
    stats.put(metric, metricValues);
  }

  def printStats = {

    println("Printing statistics:\n");
    stats.keys.foreach(metric => {
      val labels = stats.getOrElse(metric, null);
      val totals: List[Integer] = labels.values.map(v => { v._2 }).toList;
      val sum: Integer = totals.reduceLeft(_ + _);
      println("Metric = " + metric + " (" + sum + ")");

      labels.values.toList.sortWith(_._1.toString() < _._1.toString()).foreach(value => {
        println("> " + value._1 + ": " + value._2)
      })
      println

    })
  }

}
