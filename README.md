nextprot-parsers
================

Installation
------------

Configure your Scala Eclipse IDE by running `sbt eclipse` on the modules you want to add

Deployment
----------

To deploy locally:

```
sbt publish-local
```

To deploy on nexus (in you need credentials configured on ~/.sbt/x.x/sonatype.sbt):

````
credentials += Credentials("Sonatype Nexus Repository Manager", "miniwatt.isb-sib.ch", "$sonatype_username", "$sonatype_password")
```

It will go to either snapshot or production repository depending on your version suffix. It it ends with -SNAPSHOT it goes to the snapshot repository otherwise it will go to the production repository. Note that when you publish to the production repository you need to specify a new version `set version:="x.x.x"` .

Publish nextprot-parser-core
----------------------------

```
cd core
sbt
> set version:="0.33.0"
> publish
```

Publish nextprot-parser-hpa
---------------------------

```
cd hpa
sbt
> set version:="0.37.0"
> publish
> read note below:
```

IMPORTANT NOTE 
--------------

'publish' will take into account the dependency to nextprot-parser-core as it is defined in the hpa/build.sbt
So you may have to manually change the dependency in this file, example:

``
libraryDependencies ++= Seq(
  ...
  "org.nextprot.parser.core" % "nextprot-parser-core" % "0.32.0"
```

A non SNAPSHOT publishing should depend on non SNAPSHOT jars !!!

TEST
----

To test simply use ```sbt test```but you can also use ``` test-only org.nextprot.parser.hpa.subcell.FullFileEntrySubcellTest ``` if you want to specify only one test

You can configure project dependencies in eclipse
-------------------------------------------------

On eclipse define project dependencies and remove library (the jar)
