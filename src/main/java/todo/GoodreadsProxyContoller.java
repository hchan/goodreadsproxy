package todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.resthub.common.util.PostInitialize;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.bcit.hchan.webapp.App;


@Controller
@RequestMapping("/goodreadsproxy/**")
public class GoodreadsProxyContoller {
	public static String RETURN_JSP = "/goodreadsjsp/goodreadsproxy.jsp";
	public static String AUTHENTICATE_URL = "/goodreads/authenticate";
	
	public static Logger log = Logger.getLogger(GoodreadsProxyContoller.class);
	
	@RequestMapping(method=RequestMethod.GET)
	public void doAllGet(HttpServletRequest req, 
	        HttpServletResponse resp) throws ClientProtocolException, IOException, ServletException {
		String requestURLNormalized = req.getRequestURL().toString();
		requestURLNormalized = requestURLNormalized.substring(requestURLNormalized.indexOf("/goodreadsproxy/"));
		requestURLNormalized = requestURLNormalized.replace("/goodreadsproxy/", "");
		log.info("requestURLNormalized:" + requestURLNormalized);
		/*
		HttpClient client = HttpClientBuilder.create().build();
		String url = "http://" + req.getPathInfo().replace("/goodreadsproxy/", "");
		if (req.getQueryString()!=null) {
			url += "?" + req.getQueryString();
		}
		*/
		OAuthService service = App.getGoodreadsService();
		Token requestToken = (Token) App.getSessionMap(req).get("requestToken");
		
		if (App.getSessionMap(req).get("authorizeOK") != null && requestToken != null) {
			Token accessToken = (Token) App.getSessionMap(req).get("accessToken");
			if (accessToken == null) {
				accessToken = service.getAccessToken(requestToken, new Verifier(""));
				App.getSessionMap(req).put("accessToken", accessToken);
			}
			OAuthRequest request = new OAuthRequest(Verb.GET,
					"http://" + requestURLNormalized); 
			service.signRequest(accessToken, request);
			
			Response response = request.send();
			
			req.setAttribute("oauthResponse", response.getBody());
			RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher(RETURN_JSP);
			dispatcher.forward(req,resp);
			
		} else {
			String redirectURL = req.getRequestURL().toString();
			int indexOfThirdSlash = redirectURL.indexOf("/", "http://".length());
			redirectURL = redirectURL.substring(0, indexOfThirdSlash);
			redirectURL +=  req.getContextPath() + AUTHENTICATE_URL;
			log.info("redirectURL: " + redirectURL);
			req.setAttribute("oauthResponse", "REDIRECT: " + redirectURL);
			RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher(RETURN_JSP);
			dispatcher.forward(req,resp);
		}
		
	}

	private void doResponse(HttpServletResponse resp, HttpClient client,
			HttpRequestBase httpRequestBase) throws IOException, ClientProtocolException {
		HttpResponse response = client.execute(httpRequestBase);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line = "";
		while ((line = rd.readLine()) != null) {
			resp.getWriter().print(line);
		}
		
		resp.setHeader("Access-Control-Allow-Origin", "*"); // CORS
		resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		//resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
		resp.flushBuffer();
		rd.close();
	}
	
	@RequestMapping(method=RequestMethod.OPTIONS)
	public void doAllOptions(HttpServletRequest req, 
	        HttpServletResponse resp) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		String url = "http://" + req.getPathInfo().replace("/cors/", "");
		if (req.getQueryString()!=null) {
			url += "?" + req.getQueryString();
		}
		HttpOptions options = new HttpOptions(url);
		//options.addHeader("Authorization", req.getHeader("Authorization"));
		doResponse(resp, client, options);
	}
}
