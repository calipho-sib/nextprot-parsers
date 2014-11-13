Set the eclipse vm arguments (in run configurations):

-Dparser.impl="org.nextprot.parser.hpa.subcell.HPANXParser"
-Dfiles.directory=/tmp/hpa-data 
-Dfiles.expression="""^ENSG.*\.xml$"""
-Dhpa.mapping.file=src/test/resources/HPA_Subcell_Mapping.txt