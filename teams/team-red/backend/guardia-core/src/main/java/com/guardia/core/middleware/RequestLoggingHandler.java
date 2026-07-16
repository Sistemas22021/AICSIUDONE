package com.guardia.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class RequestLoggingHandler extends AbstractRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingHandler.class);

    @Override
    protected boolean doHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        StringBuilder sb = new StringBuilder();
        sb.append("[REQUEST]\n");
        sb.append(method).append(" ").append(uri).append("\n");
        sb.append("IP: ").append(ip).append("\n");
        sb.append("Fecha: ").append(now);

        logger.info(sb.toString());
        return true; // always continue
    }
}
