package io.xol.chunkstories.api.events.client;

import io.xol.chunkstories.api.client.ClientInterface;
import io.xol.chunkstories.api.events.CancellableEvent;
import io.xol.chunkstories.api.events.EventListeners;
import io.xol.chunkstories.api.input.Input;

//(c) 2015-2017 XolioWare Interactive
//http://chunkstories.xyz
//http://xol.io

/** Called when the client presses an input of some sort. */
public class ClientInputPressedEvent extends CancellableEvent {
	// Every event class has to have this

	static EventListeners listeners = new EventListeners(ClientInputPressedEvent.class);

	@Override
	public EventListeners getListeners() {
		return listeners;
	}

	public static EventListeners getListenersStatic() {
		return listeners;
	}

	// Specific event code

	public ClientInputPressedEvent(ClientInterface client, Input input) {
		this.client = client;
		this.input = input;
	}

	private final ClientInterface client;
	private final Input input;

	public ClientInterface getClient() {
		return client;
	}

	public Input getInput() {
		return input;
	}
}
