Set the eclipse vm arguments (in run configurations):

Run applications
----------------

There are 4 applications to parse different resources:

-	Antibodies
-	Subcellular location
-	Expression
-	Experimental context

```
sbt run
```

```
Multiple main classes detected, select one to run:

 [1] org.nextprot.parser.hpa.HPAAntibodyNXParserApp
 [2] org.nextprot.parser.hpa.expression.HPAExpressionNXParserApp
 [3] org.nextprot.parser.hpa.subcell.HPASubcellNXParserApp
 [4] org.nextprot.parser.hpa.expcontext.HPAExpcontextNXParserApp

Enter number:
```

Testing
----------
* sbt assembly will produce a jar in target folder
to test the jar file, one can use it like this:

```shell
java -Dfiles.directory="/scratch/ENS/G00/000" -Dfiles.expression=^ENSG.*.xml$ -Dhpa.mapping.file=HPA_Subcell_Mapping.txt -cp target/nextprot-parser-hpa.jar org.nextprot.parser.hpa.subcell.HPASubcellNXParserApp
```

Production (old doc)
----------

```
-Dparser.impl="org.nextprot.parser.hpa.subcell.HPANXParser"
-Dfiles.directory=/tmp/hpa-data
-Dfiles.expression="""^ENSG.*.xml$"""
-Dhpa.mapping.file=src/test/resources/HPA_Subcell_Mapping.txt
```

Publication
-----------

See ../README.md
