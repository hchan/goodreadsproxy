package ca.bcit.hchan.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

public class TestOAuth {

	public static void main(String[] args) throws ClientProtocolException, IOException {

		OAuthService service = new ServiceBuilder()
				.apiKey("kBalTLaEbuAf3GWqfN3nw")
				.apiSecret("zP2xpCd5Vu8aGpNw67U3PoYZOUf1QvzFPcEB8Bt3Fj0")
				.provider(new GoodreadsApi()).build();

		Token requestToken = service.getRequestToken();
		System.out.println(requestToken);
		String authUrl = service.getAuthorizationUrl(requestToken);
		System.out.println(authUrl);
	
		doOOBVerification(authUrl);

		Verifier v = new Verifier("verifier you got from the user");
		Token accessToken = service.getAccessToken(requestToken, v); // the
																		// requestToken
																		// you
																		// had
																		// from
																		// step
																		// 2

		System.out.println(accessToken);
	}

	// OutOfBand Verification
	private static void doOOBVerification(String authUrl) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		//webClient.
		 final HtmlPage page = (HtmlPage) webClient.getPage(authUrl);
		page.getHtmlElementById("user_email").setAttribute("value", "hchan@apache.org");
		page.getHtmlElementById("user_password").setAttribute("value", "abc123");
		List<HtmlElement> elements = (List<HtmlElement>) page.getByXPath("//input[@value='Sign in']");
		 Page response = elements.get(0).click();
		System.out.println(response);
		
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
