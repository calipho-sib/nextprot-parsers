check that the latest mapping file (sample IDs/MDATA association) is there,
check that the pointer to input file(s) in build.sbt is correct then:
sbt run
This parser takes the phosphoset of peptide-atlas in a tsv file and produces output.xml
which is a list of peptides objects with their associated PTM features and evidences.

