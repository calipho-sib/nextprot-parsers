# neXtProt - The knowledge resource on human proteins

This is a code repository for the SIB - Swiss Institute of Bioinformatics CALIPHO group neXtProt project

See: https://www.nextprot.org/

# neXtProt-parsers

##Installation

Configure your Scala Eclipse IDE by running `sbt eclipse` 

##Deployment

# Deploying/publishing locally:

```
sbt publish-local
```

# Deploying/publishing on nexus 

if you need credentials configured on ~/.sbt/x.x/sonatype.sbt:

```
credentials += Credentials("Sonatype Nexus Repository Manager", "miniwatt.isb-sib.ch", "$sonatype_username", "$sonatype_password")
```

It will go to either snapshot or production repository depending on your version suffix. 
If it ends with -SNAPSHOT it goes to the snapshot repository 
otherwise it will go to the production repository. 
Note that when you publish to the production repository you need to specify a new version within sbt with: 
`set version:="x.x.x"` .

```
sbt
> set version:="1.1.7"
> publish
> exit
```

## Updating dependency in nextprot-loaders and build fat jar for NP1 data integration

1. Update artefact version in https://gitlab.isb-sib.ch/calipho/nextprot-loaders/blob/develop/tools.integration/pom.xml

```
<dependency>
  <groupId>org.nextprot.parser</groupId>
  <artifactId>nextprot-scala-parsers</artifactId>
  <version>1.1.6</version>
</dependency>

```

2. Commit / push your change

3. Rebuild the fat jar on cactus (NP1 data integration server)
```
> ssh npteam@cactus
> cd /work/projects/integration/nextprot-loaders/tools.integration
> git pull origin develop
> mvn package
```

The resulting fat jar is in:
```
> ls -l /work/projects/integration/nextprot-loaders/tools.integration/target 
```


## TESTS

To test simply use ```sbt test``` but you can also use ```test-only org.nextprot.parser.hpa.subcell.FullFileEntrySubcellTest``` if you want to specify only one test

You can configure project dependencies in eclipse
-------------------------------------------------

On eclipse define project dependencies and remove library (the jar)
