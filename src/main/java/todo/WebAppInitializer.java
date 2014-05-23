package todo;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.FilterRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;

import org.apache.jasper.servlet.*;
import org.springframework.web.*;
import org.springframework.web.context.*;
import org.springframework.web.context.support.*;
import org.springframework.web.servlet.*;
import ca.bcit.hchan.webapp.AllFilter;

/**
 * This class replace the "old" web.xml and is automatically scanned at the
 * application startup
 */
public class WebAppInitializer implements WebApplicationInitializer {

	private static final String JSP_SERVLET_NAME = "jsp";
	private static final String DISPATCHER_SERVLET_NAME = "dispatcher";

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
		appContext.getEnvironment().setActiveProfiles("resthub-jpa",
				"resthub-web-server", "resthub-client-logging");
		String[] locations = { "classpath*:resthubContext.xml",
				"classpath*:applicationContext.xml" };
		appContext.setConfigLocations(locations);
		
		
		
		
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				"dispatcher", new DispatcherServlet(appContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		registerJspServlet(servletContext);
		
		Dynamic filter = servletContext.addFilter("allFilter", new AllFilter());
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
				true, "/*");

		servletContext.addListener(new ContextLoaderListener(appContext));
	}

	private void registerJspServlet(ServletContext aContext) {
		ServletRegistration.Dynamic dispatcher = aContext.addServlet(
				JSP_SERVLET_NAME, new JspServlet());
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("*.jsp");
	}
}
