package me.SebiZocer.SkinLoader.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CallScoreboardEvent extends Event {
	
	static HandlerList handlers = new HandlerList();
	
	boolean cancelled = false;
	
	public CallScoreboardEvent(){
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
}