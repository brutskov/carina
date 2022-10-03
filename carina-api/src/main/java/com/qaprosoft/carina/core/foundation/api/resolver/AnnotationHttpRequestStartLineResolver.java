package com.qaprosoft.carina.core.foundation.api.resolver;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethod;
import com.qaprosoft.carina.core.foundation.api.annotation.Endpoint;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;

public class AnnotationHttpRequestStartLineResolver extends HttpRequestStartLineResolver {

    @Override
    public HttpMethodType getMethodType(AbstractApiMethod apiMethod) {
        Endpoint e = apiMethod.getClass().getAnnotation(Endpoint.class);
        return e.methodType();
    }

    @Override
    public String getMethodPath(AbstractApiMethod apiMethod) {
        Endpoint e = apiMethod.getClass().getAnnotation(Endpoint.class);
        return e.url();
    }

    @Override
    public boolean isSupported(Class<?> clazz) {
        return clazz.isAnnotationPresent(Endpoint.class);
    }
}
