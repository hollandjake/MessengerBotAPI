import bot.MBotAPI;
import bot.utils.Config;
import bot.utils.message.Message;

class MBotAPITest extends MBotAPI {

	public MBotAPITest(Config config) {
		super(config);
	}

	public static void main(String[] args) {
		String configFile = args.length > 0 ? args[0] : null;
		new MBotAPITest(new Config(configFile));
	}

	@Override
	public void newMessage(Message message) {
		message.prettyPrint();
	}
}