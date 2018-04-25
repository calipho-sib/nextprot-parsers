package org.nextprot.parser.hpa.subcell.cases
import org.nextprot.parser.core.datamodel._
import org.nextprot.parser.core.constants.NXQuality._
import org.nextprot.parser.hpa.commons.constants.HPAReliabilityValue._
import org.nextprot.parser.core.exception._

/**
 * Case where the RNA sequence was not detected in one or more cell lines (this is not an error, but the entry must be discarded)
 */
object CASE_RNA_NOT_DETECTED extends DiscardCase("The RNA sequence was not detected in one or more cell lines");

/**
 * Case where the quality is too low to be included in NextProt (this is not an error, but the entry must be discarded)
 */
object CASE_BRONZE_QUALITY extends DiscardCase("The quality is too low to be included in NextProt");

/**
 * Case where there is no subcellular location data available for the entry (this is not an error, but the entry must be discarded)
 */
object CASE_NO_SUBCELLULAR_LOCATION_DATA extends DiscardCase("There is no subcellular location data available for the entry");

/**
 * Case where there is no Uniprot / Swissprot mapping for the entry (this is not an error, but the entry must be discarded)
 */
object CASE_NO_UNIPROT_MAPPING extends DiscardCase("There is no Uniprot / Swissprot mapping for the entry");
/**
 * Case when there are multiple Uniprot / Swissprot mapping for the entry (this is not an error, but the entry must be discarded)
 */
object CASE_MULTIPLE_UNIPROT_MAPPING extends DiscardCase("There is multiple Uniprot / Swissprot mapping for the entry");

/**
 * Case where the subcellular mapping for the given location is not applicable in the domain of NextProt (it appears a '-' in the mapping file) (this is not an error, but the entry must be discarded)
 */
object CASE_SUBCELULLAR_MAPPING_NOT_APPLICABLE extends DiscardCase("The subcellular mapping for the given location is not applicable in the domain of NextProt (it appears a '-' in the mapping file) ");

/**
 * Case when the antibody used for the experiment is not protein specific (multi-targeting)
 */
object CASE_MULTITARGETING_ANTIBODY extends DiscardCase("The antibodies used in the experiment is not specific (multi-targeting)");

/**
 * Value for protein array verification we don't expect to see in the data and for which no quality rule exists
 */

object CASE_NO_RULE_FOR_PA_NOT_SUPPORTIVE extends DiscardCase("Antibody with non supportive verification in protein array");

/**
 * Value for RNA tissueExpression when several ENSG are associated with same NP entry (eg: ENSG00000236737,  ENSG00000237671, ... for GAGE12)
 */

object CASE_MULTIPLE_ENSG_FOR_ENTRY_LEVEL_RNAEXP extends DiscardCase("RNA tissueExpression with severalENSGs for same entry level");

/**
 * Value for tissueExpression assayType="tissue" technology="IHC" type="xxx" but no data (eg: ENSG00000269556, ENSG00000004846)
 */

object CASE_NO_TISSUE_DATA_FOR_ENTRY_LEVEL extends DiscardCase("tissueExpression at entry level (not antibody level) but no data");

