package com.imooc.useredgeserviceclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.imooc.dto.UserDTO;
import okhttp3.*;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public abstract class LoginFilter implements Filter {

    private static Cache<String, UserDTO> cache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        UserDTO userDTO = null;
        String token = request.getParameter("token");
        if (StringUtils.isEmpty(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                    }
                }
            }
        }
        if (!StringUtils.isEmpty(token)){
            userDTO = cache.getIfPresent(token);
            if (userDTO == null) {
                userDTO = requestUserInfo(token);
                if (userDTO != null) {
                    cache.put(token, userDTO);
                }
            }
        }
        if (userDTO == null) {
            response.sendRedirect("http://localhost/user/login");
            return;
        }
        System.out.println(userDTO.toString());

        login(request, response, userDTO);

        filterChain.doFilter(request, response);
    }

    protected abstract void login(HttpServletRequest request, HttpServletResponse response, UserDTO userDTO);


    public UserDTO requestUserInfo(String token) {
        String url = "http://localhost/user/authentication";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", token)
                .build();
        InputStream inputStream = null;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                System.out.println(response.body().string());
                throw new RuntimeException("request user info failed :" + response.message());
            }
            inputStream = response.body().byteStream();
            byte[] bytes = new byte[1024];
            StringBuffer sb = new StringBuffer();
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                sb.append(new String(bytes, 0, len));
            }

            UserDTO userDTO = new ObjectMapper().readValue(sb.toString(), UserDTO.class);
            return userDTO;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void destroy() {

    }


}
