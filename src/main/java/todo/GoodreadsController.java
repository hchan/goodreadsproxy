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
@RequestMapping("/goodreadsproxy")
public class GoodreadsController {

	@RequestMapping("/authenticate")
	public void authenticate(
			HttpServletRequest req,
			HttpServletResponse resp,
			@RequestParam(value = "callbackURL", required = true, defaultValue = "") String callbackURL) {
		App.log.info("callbackURL: " + callbackURL);
		req.getSession().setAttribute("callbackURL", callbackURL);
		OAuthService service = App.getGoodreadsService();
		Token requestToken = service.getRequestToken();
		req.getSession().setAttribute("requestToken", requestToken);
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
		req.getSession().setAttribute("authorizeOK", true);
		try {
			resp.sendRedirect(req.getContextPath() + "/goodreadsjsp/authorizeOK.jsp");
		} catch (IOException e) {
			App.log.error("", e);
		}
	}
}
