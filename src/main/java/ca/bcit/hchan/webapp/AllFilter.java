package ca.bcit.hchan.webapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class AllFilter implements Filter {

	@Override
	public void init(FilterConfig filterconfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletrequest,
			ServletResponse servletresponse, FilterChain filterchain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) servletrequest;
		HttpServletResponse resp = (HttpServletResponse) servletresponse;
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("Access-Control-Allow-Origin", "*"); // CORS
		resp.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
		//resp.setHeader("Access-Control-Request-Headers", "authorization,x-requested-with");
		resp.setHeader("Access-Control-Allow-Headers", req.getHeader("Access-Control-Request-Headers")); // 
		
		//resp.addHeader("Access-Control-Allow-Headers", "Content-Type");
		HttpSession session = req.getSession();
		if (req.getRequestURI().endsWith("/")) {
			resp.sendRedirect(req.getRequestURI() + "index.html");
		
		} else {
			filterchain.doFilter(servletrequest, servletresponse);
		}
	}

	@Override
	public void destroy() {

	}

}
