package com.unitron.requestbin.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unitron.requestbin.model.TabKeyListener;

@Configuration
public class CustomEventRegisterFilter  extends OncePerRequestFilter {

	private static final String TAB_KEY = "Tab-Key";
	private TabKeyListener listener;


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(request.getHeader(TAB_KEY) != null) {
			listener.onRequest(request.getHeader(TAB_KEY));
		}
		
		filterChain.doFilter(request, response);
	}

	@Override
	protected void initFilterBean() throws ServletException {
		//find tabKeyListener instance (can not be loaded like classic bean)
		if(listener == null) {
			listener = WebApplicationContextUtils.
			  getRequiredWebApplicationContext(getServletContext()).
			  getBean(TabKeyListener.class);
		}
		super.initFilterBean();
	}

	@Bean
	public FilterRegistrationBean<GenericFilterBean> myFilterBean(){
		final FilterRegistrationBean<GenericFilterBean> filterRegBean=new FilterRegistrationBean<GenericFilterBean>();
		filterRegBean.setFilter(new CustomEventRegisterFilter());
		filterRegBean.setEnabled(Boolean.TRUE);
		filterRegBean.setName("CustomEventRegisterFilter");
		filterRegBean.setAsyncSupported(Boolean.TRUE);
		return filterRegBean;
	}

}
