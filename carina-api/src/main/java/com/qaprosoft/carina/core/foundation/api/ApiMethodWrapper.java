package com.qaprosoft.carina.core.foundation.api;

@FunctionalInterface
public interface ApiMethodWrapper extends ApiResult {

    void prepare(AbstractApiMethodV2 apiMethod);

}
