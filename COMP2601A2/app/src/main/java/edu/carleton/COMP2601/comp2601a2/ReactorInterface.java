package edu.carleton.COMP2601.comp2601a2;

/*---------------------------
- Avery Vine		100999500
- Alexei Tipenko	100995947
---------------------------*/

public interface ReactorInterface {
	public void register(String type, EventHandler event);
	public void deregister(String type);
	public void dispatch(Event event) throws NoEventHandler;
}
