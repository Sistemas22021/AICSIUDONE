package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import com.ccc.sistema_balistico.core.infrastructure.in.web.middleware.AuthenticationHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        String operator = "perito"; // Valor por defecto si opera fuera de un contexto web
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                Object usernameAttr = request.getAttribute(AuthenticationHandler.ATTR_AUTHENTICATED_USERNAME);
                if (usernameAttr != null && !((String) usernameAttr).isBlank()) {
                    operator = (String) usernameAttr;
                } else {
                    String xUser = request.getHeader("X-User-Name");
                    if (xUser != null && !xUser.isBlank()) {
                        operator = xUser.trim();
                    }
                }
            }
        } catch (Exception e) {
            // Ignorado (ejecución de scripts automáticos o tests unitarios sin hilo HTTP)
        }
        customRevisionEntity.setOperator(operator);
    }
}
