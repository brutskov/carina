package com.qaprosoft.carina.core.foundation.api.resolver;

import com.qaprosoft.carina.core.foundation.api.AbstractApiMethod;
import com.qaprosoft.carina.core.foundation.api.annotation.ContentType;
import com.qaprosoft.carina.core.foundation.api.http.ContentTypeEnum;
import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

public abstract class HttpRequestStartLineResolver {

    public abstract HttpMethodType getMethodType(AbstractApiMethod apiMethod);

    public abstract String getMethodPath(AbstractApiMethod apiMethod);

    public abstract boolean isSupported(Class<?> clazz);

    public String resolveContentTypeFromAnnotation(AnnotatedElement annotatedElement) {
        ContentType contentTypeA = annotatedElement.getAnnotation(ContentType.class);
        return contentTypeA != null ? contentTypeA.type() : ContentTypeEnum.JSON.getStringValues()[0];
    }

    public ContentTypeEnum resolveContentTypeEnumFromAnnotation(AnnotatedElement annotatedElement) {
        String contentTypeEnum = resolveContentTypeFromAnnotation(annotatedElement);
        return resolveContentType(contentTypeEnum);
    }

    private ContentTypeEnum resolveContentType(String contentType) {
        return Arrays.stream(ContentTypeEnum.values())
                .filter(ct -> ArrayUtils.contains(ct.getStringValues(), contentType))
                .findFirst()
                .orElse(ContentTypeEnum.NA);
    }

    public boolean isLazyInitialization() {
        return false;
    }
}
