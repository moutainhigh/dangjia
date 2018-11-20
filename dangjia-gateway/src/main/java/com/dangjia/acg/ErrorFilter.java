package com.dangjia.acg;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorFilter extends SendErrorFilter {
    Logger log = LoggerFactory.getLogger(ErrorFilter.class);
    @Override
    public String filterType() {
        return "error";
    }
    @Override
    public int filterOrder() {
        return 10;
    }
    @Override
    public boolean shouldFilter() {
        return true;
    }
    @Override
    public Object run() {
        try {
            RequestContext ex = RequestContext.getCurrentContext();
            ZuulException exception = this.findZuulException(ex.getThrowable());
            HttpServletRequest request = ex.getRequest();

            request.setAttribute("javax.servlet.error.status_code", Integer.valueOf(exception.nStatusCode));
            log.warn("Error during filtering", exception);
            request.setAttribute("javax.servlet.error.exception", exception);
            if(StringUtils.hasText(exception.errorCause)) {
                request.setAttribute("javax.servlet.error.message", exception.errorCause);
            }
            HttpServletResponse response = ex.getResponse();
            response.setContentType("application/json");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error/500");

            if(dispatcher != null) {
                ex.set("sendErrorFilter.ran", Boolean.valueOf(true));
                if(!ex.getResponse().isCommitted()) {
                    dispatcher.forward(request, response);
                }
            }
        } catch (Exception var5) {
            ReflectionUtils.rethrowRuntimeException(var5);
        }

        return null;
    }
    ZuulException findZuulException(Throwable throwable) {
        return throwable.getCause() instanceof ZuulRuntimeException ?(ZuulException)throwable.getCause().getCause():(throwable.getCause() instanceof ZuulException?(ZuulException)throwable.getCause():(throwable instanceof ZuulException?(ZuulException)throwable:new ZuulException(throwable, 500, (String)null)));
    }
}