package com.qaprosoft.carina.core.foundation.api.resolver;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethod;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.utils.R;
import org.apache.commons.lang3.StringUtils;

public class PropsHttpRequestStartLineResolver extends HttpRequestStartLineResolver {

    @Override
    public HttpMethodType getMethodType(AbstractApiMethod apiMethod) {
        HttpMethodType result;

        String typePath = R.API.get(apiMethod.getClass().getSimpleName());
        if (typePath.contains(":")) {
            result = HttpMethodType.valueOf(typePath.split(":")[0]);
        } else {
            result = HttpMethodType.valueOf(typePath);
        }
        return result;
    }

    @Override
    public String getMethodPath(AbstractApiMethod apiMethod) {
        String result = null;

        String typePath = R.API.get(apiMethod.getClass().getSimpleName());
        if (typePath.contains(":")) {
            result = StringUtils.substringAfter(typePath, ":");
        }
        return result;
    }

    @Override
    public boolean isSupported(Class<?> clazz) {
        String typePath = R.API.get(clazz.getSimpleName());
        return !StringUtils.isBlank(typePath);
    }
}
