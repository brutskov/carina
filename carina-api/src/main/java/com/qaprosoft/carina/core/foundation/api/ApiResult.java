package com.qaprosoft.carina.core.foundation.api;

import io.restassured.response.Response;

public interface ApiResult {

    ApiMethodWrapper DEFAULT = apiMethod -> {};

    default Response getResult() {
        return null;
    }

}
