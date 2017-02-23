package edu.carleton.comp2601.lecture9;

import java.io.Serializable;

import edu.carleton.comp2601.lecture9.Fields;

public class Header implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7729816603167728273L;
	public String id;	// Identity of sender; e.g., Bob
	public String recipient; // Identity of recipient; e.g. Joe
	public long seqNo;	// Sequence number for message
	public String retId;	// Return identity for routing
	public String type;		// Type of message (for reactor usage)
	
	public Header() {
		id = Fields.DEFAULT;
		recipient = Fields.DEFAULT;
		retId = Fields.DEFAULT;
		type = Fields.NO_ID;
		seqNo = Fields.DEFAULT_SEQ_ID;
	}
}
