package todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.resthub.common.util.PostInitialize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/cors/{remainder:.*}")
public class CorsContoller {
	
	@RequestMapping(method=RequestMethod.GET)
	public void doAllGet(HttpServletRequest req, 
	        HttpServletResponse resp) throws ClientProtocolException, IOException {
		System.out.println(req.getPathInfo());
		System.out.println(req.getQueryString());
		HttpClient client = HttpClientBuilder.create().build();
		String url = "http://" + req.getPathInfo().replace("/cors/", "");
		if (req.getQueryString()!=null) {
			url += "?" + req.getQueryString();
		}
		HttpGet get = new HttpGet(url);
		Enumeration<String> enumHeaderNames = req.getHeaderNames();
		//while (enumHeaderNames.hasMoreElements()) {
			//String headerName = enumHeaderNames.nextElement();
			//String headerValue = req.getHeader(headerName);
			//get.addHeader(headerName, headerValue);
		//}
		get.addHeader("Authorization", req.getHeader("Authorization"));
		
		doResponse(resp, client, get);
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
