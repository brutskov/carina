package com.qaprosoft.carina.core.foundation.api;

import java.lang.reflect.Method;
import java.util.Properties;

public class MethodBasedApiMethodV2 extends AbstractApiMethodV2 {

    private final Method method;

    public MethodBasedApiMethodV2(Method method) {
        super();
        this.method = method;
        initParams();
    }

    public MethodBasedApiMethodV2(Method method, String rqPath, String rsPath, String propertiesPath) {
        super(rqPath, rsPath, propertiesPath);
        this.method = method;
        initParams();
    }

    public MethodBasedApiMethodV2(Method method, String rqPath, String rsPath, Properties properties) {
        super(rqPath, rsPath, properties);
        this.method = method;
        initParams();
    }

    public Method getMethod() {
        return method;
    }
}
