package com.unitron.requestbin.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import com.unitron.requestbin.controller.RequestController;
import com.unitron.requestbin.model.TabKeyListener;

@Configuration
public class CustomEventRegisterFilterConfiguration  {


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
