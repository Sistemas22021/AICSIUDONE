package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RequestHandler {
    void setNext(RequestHandler next);
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
