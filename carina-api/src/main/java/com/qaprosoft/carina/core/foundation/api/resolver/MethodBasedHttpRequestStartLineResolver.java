package com.qaprosoft.carina.core.foundation.api.resolver;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethod;
import com.qaprosoft.carina.core.foundation.api.MethodBasedApiMethodV2;
import com.qaprosoft.carina.core.foundation.api.annotation.v2.ApiMapping;
import com.qaprosoft.carina.core.foundation.api.annotation.v2.EndpointMapping;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodBasedHttpRequestStartLineResolver extends HttpRequestStartLineResolver {

    @Override
    public HttpMethodType getMethodType(AbstractApiMethod apiMethod) {
        MethodBasedApiMethodV2 methodBasedApiMethod = (MethodBasedApiMethodV2) apiMethod;
        Method method = methodBasedApiMethod.getMethod();

        EndpointMapping endpointAnnotation = method.getAnnotation(EndpointMapping.class);
        return endpointAnnotation.methodType();
    }

    @Override
    public String getMethodPath(AbstractApiMethod apiMethod) {
        MethodBasedApiMethodV2 methodBasedApiMethod = (MethodBasedApiMethodV2) apiMethod;
        Method method = methodBasedApiMethod.getMethod();

        String globalPath = resolveGlobalPath(method);
        EndpointMapping endpointAnnotation = method.getAnnotation(EndpointMapping.class);

        return globalPath != null
                ? buildPath(globalPath, endpointAnnotation.path())
                : endpointAnnotation.path();
    }

    @Override
    public boolean isSupported(Class<?> clazz) {
        return clazz.isAssignableFrom(MethodBasedApiMethodV2.class);
    }

    @Override
    public boolean isLazyInitialization() {
        return true;
    }

    private String resolveGlobalPath(Method method) {
        String globalPath = null;

        boolean apiMappingPresents = method.getDeclaringClass().isAnnotationPresent(ApiMapping.class);
        if (apiMappingPresents) {
            ApiMapping apiMapping = method.getDeclaringClass().getAnnotation(ApiMapping.class);
            globalPath = apiMapping.path();
        }

        return globalPath;
    }

    private static String buildPath(String... pathSlices) {
        String[] preparedSlices = Arrays.stream(pathSlices)
                .map(MethodBasedHttpRequestStartLineResolver::preparePathSlice)
                .toArray(String[]::new);
        return String.join("/", preparedSlices);
    }

    private static String preparePathSlice(String pathSlice) {
        if (pathSlice.startsWith("/")) {
            pathSlice = pathSlice.substring(1);
        } else if (pathSlice.endsWith("/")) {
            pathSlice = pathSlice.substring(0, pathSlice.lastIndexOf("/"));
        } else {
            return pathSlice;
        }
        return preparePathSlice(pathSlice);
    }
}
