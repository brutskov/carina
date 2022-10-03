package com.qaprosoft.carina.core.foundation.api.annotation.v2;

import com.qaprosoft.carina.core.foundation.api.http.HttpMethodType;
import com.qaprosoft.carina.core.foundation.api.http.HttpResponseStatusType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface EndpointMapping {

    String path();

    HttpMethodType methodType();

    HttpResponseStatusType responseStatus() default HttpResponseStatusType.OK_200;

    String propertiesPath() default "";

}
