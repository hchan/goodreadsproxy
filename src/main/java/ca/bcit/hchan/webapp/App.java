package ca.bcit.hchan.webapp;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;

import ca.bcit.hchan.webapp.util.EnvironmentConfiguration;

public class App {
	public static Logger log = Logger.getLogger(App.class);

	public static EnvironmentConfiguration cfg = new EnvironmentConfiguration();

	public static OAuthService getGoodreadsService() {

		return new ServiceBuilder().provider(GoodreadsApi.class)
				.apiKey(cfg.get("key")).apiSecret(cfg.get("secret")).build();
	}

	public static HashMap<String, HashMap<String, Object>> mySessions = new HashMap<String, HashMap<String, Object>>();

	public static HashMap<String, Object> getSessionMap(HttpServletRequest req) {
		String key = req.getLocalName();
		log.info("key:" + key);
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				log.info(cookie.getName());
				log.info(cookie.getValue());
			}
		}
		if (key.equals("")) {
			key = req.getRemoteHost();
		}
		log.info(key);
		if (mySessions.get(key) == null) {
			HashMap<String, Object> mySession = new HashMap<String, Object>();
			mySessions.put(key, mySession);
		}
		return mySessions.get(key);
	}

}
