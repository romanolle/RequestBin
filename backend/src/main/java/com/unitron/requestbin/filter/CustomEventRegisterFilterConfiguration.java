package com.unitron.requestbin.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;

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
