package com.hollandjake.messengerBotAPI.message;

import com.google.errorprone.annotations.ForOverride;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;


public abstract class MessageComponent extends DatabaseObject {

	MessageComponent(Integer id) {
		super(id);
	}

	static ArrayList<MessageComponent> extractComponents(WebElement messageElement) {
		ArrayList<MessageComponent> messageComponents = new ArrayList<>();

		//Check what type it is
		messageComponents.addAll(Image.extractFrom(messageElement));
		messageComponents.addAll(Text.extractFrom(messageElement));

		return messageComponents;
	}

	@ForOverride
	static ArrayList<MessageComponent> extractFrom(WebElement messageElement) {
		throw new IllegalArgumentException("Subclass did not declare an overridden extractFrom() method.");
	}
}