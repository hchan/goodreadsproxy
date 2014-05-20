package todo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.resthub.common.util.PostInitialize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/cors")
public class CorsContoller {
	
	@RequestMapping
	public void doAll(HttpServletRequest request, 
	        HttpServletResponse response) {
	System.out.println(request.getPathInfo());
	System.out.println(request.getRequestURI());
	}
}
