package bug.creator.simservice1.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

//@Component
//public class Filter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Log thông tin request
//        logger.info("Request URL: " + request.getRequestURL());
//        logger.info("Request Method: " + request.getMethod());
//
//        // Log headers
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            logger.info("Header: " + headerName + " = " + request.getHeader(headerName));
//        }
//
//        // Log body nếu có
//        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
//            CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
//            String body = new String(cachedBodyHttpServletRequest.getInputStream().readAllBytes());
//            logger.info("Request Body: " + body);
//            filterChain.doFilter(cachedBodyHttpServletRequest, response);
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }
//}

public class Filter{}
