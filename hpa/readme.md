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

Production
----------

```
-Dparser.impl="org.nextprot.parser.hpa.subcell.HPANXParser" -Dfiles.directory=/tmp/hpa-data -Dfiles.expression="""^ENSG.*.xml$""" -Dhpa.mapping.file=src/test/resources/HPA_Subcell_Mapping.txt
```
