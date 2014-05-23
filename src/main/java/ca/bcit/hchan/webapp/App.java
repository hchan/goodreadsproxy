package ca.bcit.hchan.webapp;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

import ca.bcit.hchan.webapp.util.EnvironmentConfiguration;

public class App {
	public static Logger log = Logger.getLogger(App.class);
	

	public static EnvironmentConfiguration cfg = new EnvironmentConfiguration();
	public static OAuthService getGoodreadsService() {
		
		return new ServiceBuilder().provider(GoodreadsApi.class)
				.apiKey(cfg.get("key")).apiSecret(cfg.get("secret"))
				.build();
	}
	
	
}
