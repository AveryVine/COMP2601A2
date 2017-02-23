package edu.carleton.comp2601.lecture9;


public interface EventStream extends EventInputStream, EventOutputStream {
	public void close();
}
