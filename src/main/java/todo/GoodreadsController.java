package todo;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.bcit.hchan.webapp.App;

@Controller
@RequestMapping("/goodreads")
public class GoodreadsController {

	@RequestMapping("/authenticate")
	public void authenticate(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestParam(value = "callbackURL", required = true, defaultValue = "") String callbackURL) {
	
		App.getSessionMap(req).put("callbackURL", callbackURL);
		OAuthService service = App.getGoodreadsService();
		Token requestToken = service.getRequestToken();
		App.getSessionMap(req).put("requestToken", requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		try {
			resp.sendRedirect(authUrl);
		} catch (IOException e) {
			App.log.error("", e); 
		}
		

	}
	
	@RequestMapping("/authorizeOK")
	public void authorizeOK(
			HttpServletRequest req,
			HttpServletResponse resp) {
		App.getSessionMap(req).put("authorizeOK", true);
		try {
			resp.sendRedirect(req.getContextPath() + "/goodreadsjsp/authorizeOK.jsp");
		} catch (IOException e) {
			App.log.error("", e);
		}
	}
}
