package edu.ucsb.deepspace.business;
/**
 * There are three measure modes available. IFM, ADM, and IFMBYADM<P>
 * IFM interferometry:  cannot determine absolute position.  157 nm resolution for relative changes.<BR>
 * ADM absolute distance mode: capable of determining the absolute position, not as precise as IFM<BR>
 * IFMBYADM uses ADM for absolute position and IFM for relative changes<BR>
 * @author Reed Sanpore
 *
 */
public enum TrackerMeasureMode {
	IFM, ADM, IFMBYADM;
}