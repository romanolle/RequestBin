package com.unitron.requestbin.filter;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessControlAllowOriginFilter extends GenericFilterBean {
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		allowOrigin(resp, "*", false);
		chain.doFilter(req, resp);
	}
	
	private void allowOrigin(ServletResponse resp, String allowOriginUrl, boolean allowCredentials) {
		if (resp instanceof HttpServletResponse) {			
			HttpServletResponse response = (HttpServletResponse)resp;
			response.setHeader("Access-Control-Allow-Origin", allowOriginUrl);
			if (allowCredentials) {
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
			response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Authorization, Content-Type, Tab-Key");
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		}
	}
	
	
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public FilterRegistrationBean<GenericFilterBean> myFilterBeanAccessOrigin(){
		final FilterRegistrationBean<GenericFilterBean> filterRegBean=new FilterRegistrationBean<GenericFilterBean>();
		filterRegBean.setFilter(new AccessControlAllowOriginFilter());
		filterRegBean.setEnabled(Boolean.TRUE);
		filterRegBean.setName("Access-Control-Allow-Origin Filter");
		filterRegBean.setAsyncSupported(Boolean.TRUE);
		return filterRegBean;
	}

}