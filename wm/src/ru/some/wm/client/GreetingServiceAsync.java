package ru.some.wm.client;

import ru.some.wm.controller.Dto;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void action(Dto dto, AsyncCallback<Dto> callback);
}
