package com.cimonhe.filter;

import com.alibaba.fastjson.JSONObject;
import com.cimonhe.constant.ReturnValueConstant;
import com.cimonhe.pojo.User;
import com.cimonhe.service.MyUserDetailsService;
import com.cimonhe.service.UserService;
import com.cimonhe.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("X_token");

        String uri = request.getRequestURI();

        User user;

        String username = null;
        String jwt = null;

        if (authorizationHeader != null) {
            jwt = authorizationHeader;
            username = jwtUtil.extractUsername(authorizationHeader);
            System.out.println(username);
        }
        if (uri.contains("/register/")||uri.contains("/login/")||uri.contains("/swagger-ui.html")||uri.contains("/swagger-resources")||uri.contains("/webjars/")||uri.contains("/v2/")||uri.contains("/swagger-ui.html/")||uri.contains("/goods/")||uri.contains("/showGoods/")||uri.contains("/user/portrait/")||uri.contains("/user/otherUserProfile"))
            chain.doFilter(request, response);

        else if ((user=userService.queryUserByName(username))==null)
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/html;charset=UTF-8");
            JSONObject returnValue = new JSONObject();
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"没有携带token或者该token携带的用户名不存在");
            response.getWriter().print(returnValue.toString());
        }
        else if ((!redisTemplate.hasKey("tokenVersion"+user.getId()))||(!redisTemplate.opsForValue().get("tokenVersion"+user.getId()).equals(jwtUtil.extractAllClaims(authorizationHeader).get("tokenVersion"))))
        {
            System.err.println(redisTemplate.opsForValue().get("tokenVersion"+user.getId()));
            System.err.println(jwtUtil.extractAllClaims(authorizationHeader).get("tokenVersion"));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/html;charset=UTF-8");
            JSONObject returnValue = new JSONObject();
            returnValue.put(ReturnValueConstant.STATUS,ReturnValueConstant.FAIL);
            returnValue.put(ReturnValueConstant.MSG,"token已失效请重新获取");
            response.getWriter().print(returnValue.toString());
        }
        else if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                chain.doFilter(request, response);
            }
        }
    }

}
