package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractRequestHandler implements RequestHandler {
    protected RequestHandler next;

    @Override
    public void setNext(RequestHandler next) {
        this.next = next;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean proceed = doHandle(request, response);
        if (proceed && next != null) {
            next.handle(request, response);
        }
    }

    /**
     * Perform handler-specific logic. Return true to continue the chain.
     */
    protected abstract boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
