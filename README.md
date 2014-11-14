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

To deploy on nexus (in you need credentials configured on settings.xml):

It will go to either snapshot or production repository depending on your version suffix. It it ends with -SNAPSHOT it goes to the snapshot repository otherwise it will go to the production repository. Note that when you publish to the production repository you need to specify a new version `set version := x.x.x` .

```
sbt publish

```

You can configure project dependencies in eclipse as well
---------------------------------------------------------

On eclipse define project dependencies and remove library (the jar)
