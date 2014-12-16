This page represents behavior-driven development tests in order to validate the specs between business analysts and development unit.

The main document containing the specifications can be found here:&nbsp;[^Spec_HPA_release_12.docx]

{info:title=Specifications implemented:}
*neXtProt release:*
Database release : 2014-02-25
Application release : 3.0.22
*HPA version: 12.0*
{info}

{info:title=Changes with respect to previous version:}
The description of the annotation includes now the Note= in the mapping file instead of the description given by HPA, for example:

* Note=Associated with actin filaments. Additional location.
* Note= Associated with microtubules plus end. Main location.

APE quality rules changes:

* When (Reliability=Medium) and (Protein Array=Supportive) we get quality SILVER instead of GOLD

Single / Selected rules changes:

* When (PA =&nbsp;Supportive) AND (IF=&nbsp;Supportive) AND (WB=Uncertain) &nbsp;we get Quality&nbsp;SILVER&nbsp;instead of GOLD
* When (PA =&nbsp;Supportive) AND (IF=&nbsp;Uncertain) AND (WB=Uncertain) &nbsp;we get Quality&nbsp;SILVER&nbsp;instead of GOLD&nbsp;


CASE_MULTIPLE_UNIPROT_MAPPING was added. Before they were treated as&nbsp;CASE_NO_UNIPROT_MAPPING


The xml has changed in one way with subAssay being added to antibody / subcellular location
{info}

{toc}

h3. General specifications

|| CODE || Name || Description || Tests ||
| HPA_PARS_SPEC_G1 | Annotations type and cardinality | From a valid HPA entry, we should&nbsp;produce N annotations of type = "subcellular location" (cv-terms) with NOTE if necessary&nbsp; \\
\\
Example for an entry in plasma membrane and in cytoskeleton (microtuble plus end): \\
# Two annotations of type= "subcellular location" (cv-term)&nbsp;
## cv-term: SL-0039, Description: Main location.
## cv-term: SL-0090, Description: Note= Associated with microtubules plus end.&nbsp;Additional location. | at least 2 tests: \\
# one for cardinality&nbsp;
# one for content (type, description, additional, main ...) |
| HPA_PARS_SPEC_G2 | Type | The HPA analysis can only be \\
# SINGLE (went from 8680 to&nbsp;16 cases, good\!)
# SELECTED (33 cases)
# APE (6147 cases)
# SELECTED but treated as APE (21 cases) | 1 test |
| HPA_PARS_SPEC_G3 | Rule APE | In case of APE the rules to determine the quality are the following (total cases 6183): \\
\\
&nbsp; &nbsp; \\  !Screen Shot 2014-02-24 at 9.59.49 AM.png|border=1,width=300!\\
\\
* High, \_: 1063
* Low, Supportive: 2112
* Low, Uncertain: 717
* Medium, Supportive: 1463
* Medium, Uncertain: 540
* Very_Low, \_: 288&nbsp; \\ | Test all combinations |
| HPA_PARS_SPEC_G4 | Rule SINGLE, SELECTED | In case of SINGLE or SELECTED the rules to determine the quality are the followings (total 71 cases treated): &nbsp; \\
\\  !Screen Shot 2014-02-24 at 10.14.11 AM.png|border=1,width=300!\\
&nbsp;&nbsp; \\
* S-N-U => BRONZE: 1
* S-S-N => SILVER: 6
* S-S-S => GOLD: 16
* S-S-U => SILVER: 3
* S-U-N => SILVER: 11
* S-U-S => SILVER: 14
* S-U-U => SILVER: 5
* U-S-N => SILVER: 1
* U-S-S => SILVER: 3
* U-U-N => BRONZE: 1
* U-U-S => SILVER: 7
* U-U-U => BRONZE: 3 \\
\\ | Test all combinations |


h3. Complementary specifications

|| CODE || Name || Description || Applies to || Tests ||
| HPA_PARS_SPEC_C1 | Western blot missing | When western blot is not present define it as uncertain&nbsp;(WB = Uncertain) | 2 instead of 115, GOOD\! | 1 test |
| HPA_PARS_SPEC_C2 | Protein Array for CAB antibodies | The protein array of CAB antibodies are considered always Supportive&nbsp;(PA = Supportive; for CAB antibodies) | 17 instead of 556, is this good? | 1 test |
| HPA_PARS_SPEC_C3 | Reliability not available for APE | When the reliability is not available for APE apply the following rule and treat those cases as Reliability (Single) (Reliability (APE):N/A):&nbsp; \\
* Intensity values: negative=0, weak=1, moderate=3, strong=5
* For each of the antibodies sum the values of intensities in each cell line. The antibody with the max value is the one selected | only 1 instead of 5, GOOD\! | 1 test |
| HPA_PARS_SPEC_C4 | RNA missing | Cases without RNA information are considered as if RNA is detected \\ | 0 instead of 12, VERY GOOD\! | 1 test |


h3. Discarded cases

These cases are considered recurrent in the domain of HPA, however they should not be integrated in NextProt. (The checks are performed in the same order as they appear on the table so the first one wins, for example we don't even compute the quality if the entry is not in UniProt / Swissprot)

|| CODE || Name || Description || Applies to || Tests ||
| HPA_PARS_D1 | CASE_NO_UNIPROT_MAPPING \\ | When there is no UniProt / Swissprot mapping for the entry | 398 | 1 test |
| HPA_PARS_D2 | CASE_NO_SUBCELLULAR_LOCATION_DATA \\ | When there is no subcellular location available | 4486=>9238&nbsp;\!\!\! | 1 test |
| HPA_PARS_D3 | CASE_RNA_NOT_DETECTED | When the RNA sequence was not detected in any of the cell lines that were used to analyse the subcellular location discard the case | 400 | 1 test |
| HPA_PARS_D4 | CASE_SUBCELULLAR_MAPPING_NOT_APPLICABLE | The subcellular mapping for the given location is not applicable in the domain of NextProt (it appears a '-' in the mapping file): aggresome | 9 | 1 test |
| HPA_PARS_D6 | CASE_MULTITARGETING_ANTIBODY \\ | When the antibody is not specific (multi-targeting): \\
* In case of SINGLE \-> if is a multitargeting => bronze.
* In case of SELECTED \-> only the antibody used in the experiment should be taken into account and if this one is multitargeting => bronze.
* In case of APE \-> if one or more antibody used is multi-targeting the entry is discarded => bronze | 0 | 2 test |
| HPA_PARS_D5 | CASE_BRONZE_QUALITY \\ | When the quality is bronze | 1010 | 1 test |
| HPA_PARS_D6 | CASE_MULTIPLE_UNIPROT_MAPPING \\ | When there are more than one Swissprot entry | 39 | 1 test |

h3. Error cases

The following cases should never happen\! If they happen they are logged in a file for future retrial (failed-entries.log) and if necessary the error should be reported to HPA.

|| CODE || Error Name || Description || Applies to || Tests ||
| HPA_PARS_E1 | CASE_SUBCELULLAR_MAPPING_NOT_FOUND | The subcellular mapping for this location is not found in the current mapping file (does not exist in the mapping file) | 0 | 1 test |
| HPA_PARS_E2 | CASE_MORE_THAN_ONE_ANTIBODY_FOUND_FOR_SELECTED | More than 1 subcellular location data has been found for several antibodies in the experiment selected | 0 | 1 test |
| HPA_PARS_E3 | PROTEIN_NOT_DETECTED_BUT_LOCATION_EXISTENCE | The protein was not detected but HPA displays a subcellular location information. HPA should be feedback on this. Only happens with:&nbsp;ENSG00000007350 \\ | {color:#ff0000}0{color} | 1 test |
| HPA_PARS_E4 | CASE_IFTYPE_UNKNOWN | The IF analysis type is neither APE, SINGLE nor SELECTED | 0 | 1 test |
| |
| HPA_PARS_E0 | UNEXPECTED_ERROR | Any other error that is not handled by the previous ones | 0 | 1 test |

h3. Configuration files


h4. Mapping HPA \-> Swissprot / Uniprot

A mapping file should be provided by Paula and kept in M:
The file format should be in tab separated format (TSV) as following:

First a column with the HPA term
Second with the Swissprot / Uniprot cvterm
(Optionally) thrid column with a note (this note will not be taken into consideration in the parsing, it is only used for information purpose)
Any HPA term that we discard should be map to a dash "-"

Any line in the file can be commented using # in the beginning of the line.

The mapping file of the moment of the writing is as follows:&nbsp;[HPA subcellular location specs^HPA_Subcell_Mapping.txt]

h4. Multi targeting antibody

A file containing all mapping antibodies must be produced by an ant task
