package edu.carleton.COMP2601.comp2601a2;


public interface EventStream extends EventInputStream, EventOutputStream {
	public void close();
}