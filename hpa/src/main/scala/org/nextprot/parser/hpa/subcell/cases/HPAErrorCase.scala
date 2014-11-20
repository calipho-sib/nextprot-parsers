package org.nextprot.parser.hpa.subcell.cases
import org.nextprot.parser.core.datamodel._
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.exception._

/**
 * Error case where the sub cellular mapping for the location is not found in the current mapping file (does not exist in the mapping file)
 */
object CASE_SUBCELULLAR_MAPPING_NOT_FOUND extends ErrorCase("The subcellular mapping for this location is not found in the current mapping file (does not exist in the mapping file)");

/**
 * Error case where more than one antibody has been found for the experiment selected
 */
object CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED extends ErrorCase("More than one antibody has been found for the experiment selected");

/**
 * Error case where more than one antibody has been found for the experiment selected
 */
object CASE_NO_ANTIBODY_FOUND_FOR_EXPR extends ErrorCase("No antibody related to tissue expression with assay type tissue has been found");

/**
 * Error case where more than one antibody has been found for the experiment selected
 */
object CASE_NO_ANTIBODY_FOUND_FOR_SUBCELL extends ErrorCase("No antibody related to subcellular location");

/**
 * The protein was not detected but contradictory information related to the location is shown
 */
object PROTEIN_NOT_DETECTED_BUT_LOCATION_EXISTENCE extends ErrorCase("The protein was not detected but contradictory information related to the location is shown");

/**
 * Case where the immunofluorescence analysis type is not valid)
 */
object CASE_IFTYPE_UNKNOWN extends ErrorCase("The IF analysis type is neither APE, SINGLE nor SELECTED");

/**
 * Error case where the subcellular mapping for the location is not found in the current mapping file (does not exist in the mapping file)
 */
object CASE_ASSAY_TYPE_NOT_TISSUE extends ErrorCase("The tissue expression assay type is discarded");

