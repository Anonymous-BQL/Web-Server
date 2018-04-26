package io.github.bianql.servletApi;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;

public class ApplicationFilterChain implements FilterChain {
    private ArrayList<Filter> filters = new ArrayList<>();
    private int currentFilterIndex = 0;
    private Servlet servlet;

    public ApplicationFilterChain(Servlet servlet) {
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if(currentFilterIndex < filters.size()){
            filters.get(currentFilterIndex).doFilter(servletRequest,servletResponse,this);
        }else {
            servlet.service(servletRequest,servletResponse);
        }
    }
    public void addFilter(Filter filter){
        filters.add(filter);
    }
}
