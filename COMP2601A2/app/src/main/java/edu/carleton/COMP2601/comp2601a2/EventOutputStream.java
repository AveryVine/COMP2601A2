package edu.carleton.COMP2601.comp2601a2;

/*---------------------------
- Avery Vine		100999500
- Alexei Tipenko	100995947
---------------------------*/

import java.io.IOException;

public interface EventOutputStream {
	public void putEvent(Event e) throws IOException, ClassNotFoundException;
}
