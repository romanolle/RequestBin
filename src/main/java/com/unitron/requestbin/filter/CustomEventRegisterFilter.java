package com.unitron.requestbin.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unitron.requestbin.controller.RequestController;
import com.unitron.requestbin.model.TabKeyListener;

public class CustomEventRegisterFilter  extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomEventRegisterFilter.class);
	private static final String TAB_KEY = "Tab-Key";
	private TabKeyListener listener;
	private String contextPath;
	
	public CustomEventRegisterFilter(String contextPath) {
		this.contextPath = contextPath;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getHeader(TAB_KEY) != null) {
			String key = "";
			if(request.getServletPath().startsWith(RequestController.EVENT_PATH)) {
				key = getKey(request.getRequestURI(), RequestController.EVENT_PATH);
				//key = findKey(request.getServletPath());
			}
			LOGGER.debug("register key: {}", key);

			listener.onRequest(request.getHeader(TAB_KEY), key);
		}
		
		filterChain.doFilter(request, response);
	}
	private String getKey(String uri, String path) {
		return uri.replaceFirst("/", "")
				.replaceFirst(
						contextPath.replaceFirst("/", ""),
						""
				)
				.replaceFirst(path, "")
				.replaceFirst("/", "");
	}
//	
//	private String findKey(String url) {
//		String key = "";
//		if(url.startsWith(RequestController.EVENT_PATH)) {
//			key = url.replaceFirst(RequestController.EVENT_PATH, "");
//			if(key.length() != 0) {
//				if(key.charAt(0) != '/') {
//					return "";
//				} else {
//					key = key + "/";
//					if(key.startsWith("//")) {
//						return "";
//					}
//					Pattern pattern = Pattern.compile("(/\\w+/)");
//					Matcher matcher = pattern.matcher(key);
//					if (matcher.find()) {
//					    return matcher.group(1).replaceAll("/", "");
//					}
//				}
//			}
//		}
//		return "";
//	}

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

	@Value("${server.servlet.context-path}")
	private String beanContextPath;
	
	@Bean
	public FilterRegistrationBean<GenericFilterBean> myFilterBean(){
		final FilterRegistrationBean<GenericFilterBean> filterRegBean=new FilterRegistrationBean<GenericFilterBean>();
		filterRegBean.setFilter(new CustomEventRegisterFilter(beanContextPath));
		filterRegBean.setEnabled(Boolean.TRUE);
		filterRegBean.setName("CustomEventRegisterFilter");
		filterRegBean.setAsyncSupported(Boolean.TRUE);
		return filterRegBean;
	}

}
