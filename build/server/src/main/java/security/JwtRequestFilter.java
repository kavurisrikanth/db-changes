package security;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1) From Headers for /graphql
    // For subscriptions we set token from the Subscription class.
    // Each method
    if (request.getRequestURI().startsWith("/api/")) {
      if (Objects.equals(request.getMethod(), "OPTIONS")) {
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
      }
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Content-Type", request.getContentType());
    }
    String token = null;
    String requestTokenHeader = request.getHeader("Authorization");
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      token = requestTokenHeader.substring(7);
    } else {
      token = request.getParameter("token");
    }
    Authentication auth = null;
    MDC.put("http", request.getSession(true).getId());
    // We take token from both places.
    if (token != null) {
      UserProxy proxy = jwtTokenUtil.validateToken(token);
      if (proxy != null) {
    	  MDC.put("userId", String.valueOf(proxy.userId));
        auth = new UsernamePasswordAuthenticationToken(proxy, proxy.sessionId, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
      } else {
        // we need to reject the quest as this is invalid token
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.flushBuffer();
        return;
      }
    } else {
  	  MDC.put("userId", "0");
    }
    filterChain.doFilter(request, response);
    SecurityContextHolder.getContext().setAuthentication(null);
  }

}
