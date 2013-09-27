package org.nextprot.parser.core

/**
 * Defines the nature of the command line arguments that can be passed to the parser
 */

object NXProperties {

    /** Where to store the validated XML result file */   
  val outputFileProperty = "output.file"
   /** Where to store the parsing error report */
  val failedFileProperty = "failed.file" 
    /** what specific parser to run (eg: HPA-subcell, HPA-expression, Bgee...) */
  val parserImplementationProperty = "parser.impl";
    /** Default folder where to find the input data */
  val directoryFilesProperty = "files.directory";
    /** A regular expression to filter input files in the default folder */
  val regularExpressionProperty = "files.expression"
   /** A file explicitly containing the input files to operate on (one per line) */
  val InputFilterFileProperty = "files.listfile";
}