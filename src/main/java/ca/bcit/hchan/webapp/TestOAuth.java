package ca.bcit.hchan.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Request;
import org.scribe.model.RequestTuner;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import ca.bcit.hchan.webapp.OAuth10aServiceImpl.TimeoutTuner;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

public class TestOAuth {
	//https://github.com/fernandezpablo85/scribe-java/wiki/Getting-Started
	public static void main(String[] args) throws ClientProtocolException, IOException {
		
		String key = "kBalTLaEbuAf3GWqfN3nw";
		String secret = "zP2xpCd5Vu8aGpNw67U3PoYZOUf1QvzFPcEB8Bt3Fj0";
		DefaultApi10a provider = new GoodreadsApi();
		OAuthConfig config = new OAuthConfig(key, 
				secret,
				OAuthConstants.OUT_OF_BAND,
	    SignatureType.Header,
	    null,
				(OutputStream)null
				);
		DefaultApi10a api = provider;
		OAuth10aServiceImpl service = 
				new OAuth10aServiceImpl(api, config);
				
		
	
		Token requestToken = service.getRequestToken();
		System.out.println(requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		System.out.println(authUrl);
	
		HtmlPage htmlPage = doOOBVerification(authUrl);
		System.out.println(htmlPage.getUrl());
// https://www.google.ca/?test?oauth_token=K3U550usXQKJyGCIB5CA8Q&authorize=1
String oauth_token = htmlPage.getUrl().toString();
		oauth_token = oauth_token.substring(oauth_token.indexOf("oauth_token="));
		oauth_token = oauth_token.replace("oauth_token=", "");
		oauth_token = oauth_token.substring(0, oauth_token.indexOf("&"));
		//WebResponse webResponse = htmlPage.getWebResponse();
		Verifier v = new Verifier(oauth_token);
	
	
		Token accessToken = getAccessToken(service, provider, requestToken, v); // the
																		// requestToken
																		// you
																		// had
																		// from
																		// step
																		// 2

		makeRequest(service, accessToken);
	}

	private static void makeRequest(OAuth10aServiceImpl service, Token accessToken) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.goodreads.com/api/auth_user"); // arbitrary request
		service.signRequest(accessToken, request); // the access token from step 4
		Response response = request.send();
		System.out.println(response.getBody());
	}

	private static Token getAccessToken(OAuth10aServiceImpl service, DefaultApi10a provider, Token requestToken, Verifier v) {
	
		 OAuthRequest request = new OAuthRequest(Verb.POST, GoodreadsApi.ACCESS_TOKEN_URL);
		    request.addOAuthParameter(OAuthConstants.TOKEN, requestToken.getToken());
		    //request.addOAuthParameter(OAuthConstants.VERIFIER, v.getValue());
		    //request.addOAuthParameter("oauth_verifier", v.getValue());
		 
		    service.addOAuthParams(request, requestToken);
		    service.appendSignature(request);
		    RequestTuner tuner = new TimeoutTuner(2, TimeUnit.SECONDS);
			Response response = request.send(tuner);
		    return provider.getAccessTokenExtractor().extract(response.getBody());
	}

	// OutOfBand Verification
	private static HtmlPage doOOBVerification(String authUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		
		 final HtmlPage page = (HtmlPage) webClient.getPage(authUrl);
		page.getHtmlElementById("user_email").setAttribute("value", "hchan@apache.org");
		page.getHtmlElementById("user_password").setAttribute("value", "abc123");
		List<HtmlElement> elements = (List<HtmlElement>) page.getByXPath("//input[@value='Sign in']");
		HtmlPage response = elements.get(0).click();
		return response;
		
	}

	private static void doGetAuth(HttpClient client, String authUrl)
			throws IOException, ClientProtocolException {
		HttpGet get = new HttpGet(authUrl);
		HttpResponse response = client.execute(get);
		printResponse(response);
	}
	private static void printResponse(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		String line = "";
		while ((line = rd.readLine()) != null) {
			System.out.println(line);
		}
	}
}
