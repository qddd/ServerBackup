package com.n;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;



public class webInitializer implements WebApplicationInitializer {

    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.setServletContext(servletContext); 
    }
} 