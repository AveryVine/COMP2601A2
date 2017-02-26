package edu.carleton.COMP2601.comp2601a2;

/*---------------------------
- Avery Vine		100999500
- Alexei Tipenko	100995947
---------------------------*/

import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6394396411894185136L;
	public Header header;
	public Body body;
	
	public Message() {
		header = new Header();
		body = new Body();
	}
}
