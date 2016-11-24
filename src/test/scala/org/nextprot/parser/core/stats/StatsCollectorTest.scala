package org.nextprot.parser.core

import org.nextprot.parser.core.stats.StatisticsCollector

class StatsCollectorTest extends CoreTestBase {

  "The StatsCollectorTest " should " count successfully each metric" in {
    
    val stats = new StatisticsCollector();
    stats ++ ("test", "new");
    stats ++ ("test", "new");
    stats ++ ("test", "new");
    stats ++ ("test", "new");
    stats ++ ("test", "new");
    stats ++ ("test", "new");
    stats ++ ("test", "old");
    stats ++ ("test", "old");
    stats ++ ("test", "old");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");
    stats ++ ("test-new", "new");

    stats.printStats;

    
  }

}