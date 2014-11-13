package org.nextprot.parser.core

import org.nextprot.parser.core.stats.StatisticsCollector

class StatsCollectorTest extends CoreTestBase {

  "The StatsCollectorTest " should " count successfully each metric" in {
    
    val stats = new StatisticsCollector();
    stats.increment("test", "new");
    stats.increment("test", "new");
    stats.increment("test", "new");
    stats.increment("test", "new");
    stats.increment("test", "new");
    stats.increment("test", "new");
    stats.increment("test", "old");
    stats.increment("test", "old");
    stats.increment("test", "old");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");
    stats.increment("test-new", "new");

    stats.printStats;

    
  }

}