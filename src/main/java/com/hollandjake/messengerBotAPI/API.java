package com.hollandjake.messengerBotAPI;

import com.google.errorprone.annotations.ForOverride;
import com.hollandjake.messengerBotAPI.message.Human;
import com.hollandjake.messengerBotAPI.message.Message;
import com.hollandjake.messengerBotAPI.message.MessageThread;
import com.hollandjake.messengerBotAPI.threads.WaitForMessage;
import com.hollandjake.messengerBotAPI.util.Config;
import com.hollandjake.messengerBotAPI.util.LOG_LEVEL;
import com.hollandjake.messengerBotAPI.util.WebController;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

public abstract class API extends Thread {
	private final LOG_LEVEL logLevel;
	private final MessageThread thread;
	private final Human me;
	protected Config config;
	private WebController webController;
	private boolean running;
	private int refreshRate;
	private Duration messageTimeout;
	public API(Config config) {
		this.config = config;
		messageTimeout = Duration.ofMillis(Long.valueOf(config.getProperty("message_timeout")));
		refreshRate = (int) config.get("refresh_rate");
		this.logLevel = (LOG_LEVEL) config.get("log_level");

		//Login to the thread
		webController = new WebController(config, this);
		this.thread = webController.getThread();
		this.me = webController.getMe();
		//Load this system

		if (LOG_LEVEL.DEBUG.lessThanEqTo(logLevel)) {
			System.out.println("System is running");
		}
		running = true;

		//Create threads
		Thread.setDefaultUncaughtExceptionHandler((thread, e) -> errorHandler(e));

		//waiting for messages
		new WaitForMessage(this, webController).start();
		loaded();
	}

	public void errorHandler(Throwable e) {
		e.printStackTrace();
		System.exit(1);
	}

	public void quit() {
		webController.quit(true);
		System.exit(0);
	}

	public void checkDbConnection() {
		webController.checkDbConnection();
	}

	@ForOverride
	public void newMessage(Message message) {
	}

	@ForOverride
	public void databaseReload(Connection connection) throws SQLException {
	}

	@ForOverride
	public void loaded() {
	}

	public void sendMessage(Message message) {
		webController.sendMessage(message);
	}

	public void sendMessage(String message) {
		webController.sendMessage(Message.fromString(thread, me, message));
	}

	public boolean debugging() {
		return logLevel.greaterThanEqTo(LOG_LEVEL.DEBUG);
	}

	public Config getConfig() {
		return config;
	}

	public LOG_LEVEL getLogLevel() {
		return logLevel;
	}

	public Human getMe() {
		return me;
	}

	public Duration getMessageTimeout() {
		return messageTimeout;
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public MessageThread getThread() {
		return thread;
	}

	@ForOverride
	public String getVersion() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = null;
		try {
			try {
				model = reader.read(new FileReader(new File("pom.xml")));
			} catch (IOException e) {
				model = reader.read(
						new InputStreamReader(
								getClass().getResourceAsStream(
										"/META-INF/maven/de.scrum-master.stackoverflow/aspectj-introduce-method/pom.xml"
								)
						)
				);
			}
		} catch (XmlPullParserException | IOException ignore) {
		}
		if (model != null) {
			return model.getVersion();
		}
		return "";
	}

	public boolean isRunning() {
		return running;
	}
}
