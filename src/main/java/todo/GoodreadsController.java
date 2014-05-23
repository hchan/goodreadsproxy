package todo;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.bcit.hchan.webapp.GoodreadsApi;
import ca.bcit.hchan.webapp.OAuth10aServiceImpl;

@Controller
@RequestMapping("/goodreads")
public class GoodreadsController {

	@RequestMapping("/authenticate")
	public void authenticate(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestParam(value = "callbackURL", required = true, defaultValue = "") String callbackURL) {
		App.log.info("callbackURL: " + callbackURL);
		DefaultApi10a provider = new GoodreadsApi();
		OAuthConfig config = new OAuthConfig(GoodreadsApi.key,
				GoodreadsApi.secret, OAuthConstants.OUT_OF_BAND,
				SignatureType.Header, null, (OutputStream) null);
		DefaultApi10a api = provider;
		OAuth10aServiceImpl service = new OAuth10aServiceImpl(api, config);
		Token requestToken = service.getRequestToken();
		App.log.info(requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		App.log.info(authUrl);

	}
}
