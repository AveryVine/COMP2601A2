package edu.carleton.COMP2601.comp2601a2;

import java.io.IOException;

public interface EventInputStream {
	public Event getEvent() throws IOException, ClassNotFoundException;
}
