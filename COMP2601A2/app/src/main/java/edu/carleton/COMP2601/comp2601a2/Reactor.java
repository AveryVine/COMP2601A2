package edu.carleton.COMP2601.comp2601a2;

/*---------------------------
- Avery Vine		100999500
- Alexei Tipenko	100995947
---------------------------*/

import java.util.concurrent.ConcurrentHashMap;

public class Reactor implements ReactorInterface {
	
	ConcurrentHashMap<String, EventHandler> map;
	
	public Reactor() {
		map = new ConcurrentHashMap<String, EventHandler>();
	}

	public void register(String key, EventHandler handler) {
		map.put(key, handler);
	}
	
	public void deregister(String key) {
		map.remove(key);
	}
	
	@Override
	public void dispatch(Event event) throws NoEventHandler {
		EventHandler handler = map.get(event.type);
		if (handler != null) 
			handler.handleEvent(event);
		else
			throw new NoEventHandler(event.type);
	}
}
