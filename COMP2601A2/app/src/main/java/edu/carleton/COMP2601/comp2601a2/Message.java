package edu.carleton.comp2601.lecture9;

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
