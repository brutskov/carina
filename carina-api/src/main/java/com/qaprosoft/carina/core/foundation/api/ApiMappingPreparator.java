package com.qaprosoft.carina.core.foundation.api;

import com.qaprosoft.carina.core.foundation.api.annotation.v2.EndpointMapping;
import com.qaprosoft.carina.core.foundation.api.annotation.v2.PathVariable;
import com.qaprosoft.carina.core.foundation.api.annotation.v2.QueryParam;
import io.restassured.response.Response;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ApiMappingPreparator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApiMappingPreparator() {
    }

    public static <M> M getMapping(Class<M> clazz) {
        return getMapping(clazz, new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public static <M> M getMapping(Class<M> clazz, Object... args) {
        M result;
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);
        MethodHandler handler = prepareHandler();

        Class<?>[] types = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class[]::new);

        try {
            result = (M) factory.create(types, args, handler);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    static MethodHandler prepareHandler() {
        return (self, method, proceed, args) -> {
            Object invocationResult = proceed.invoke(self, args);

            if (isMethodValid(method)) {
                if (!(invocationResult instanceof ApiMethodWrapper)) {
                    throw new RuntimeException(String.format("Method %s should return instance of %s class", method.getName(), ApiMethodWrapper.class.getName()));
                }

                EndpointMapping endpointMapping = method.getAnnotation(EndpointMapping.class);
//                AbstractApiMethodV2 apiMethod = endpointMapping.propertiesPath() == null
//                        ? new MethodBasedApiMethodV2(method, apiMethodPreparator.getRequestTemplatePath(), apiMethodPreparator.getResponseTemplatePath(), new Properties())
//                        : new MethodBasedApiMethodV2(method, apiMethodPreparator.getRequestTemplatePath(), apiMethodPreparator.getResponseTemplatePath(), apiMethodPreparator.getPropertiesPath());
                AbstractApiMethodV2 apiMethod = new MethodBasedApiMethodV2(method);
                apiMethod.expectResponseStatus(endpointMapping.responseStatus());

                resolvePathPatterns(apiMethod, method, args);
//                apiMethod.setMethodPath(resolvedPath);

                Map<String, ?> queryParams = resolveNamedParameters(method, QueryParam.class, QueryParam::value, args);
                apiMethod.getRequest().queryParams(queryParams);

                ApiMethodWrapper apiMethodWrapper = (ApiMethodWrapper) invocationResult;
                apiMethodWrapper.prepare(apiMethod);
                Response response = apiMethod.callAPI();

//                ApiResponseWrapper responseWrapper = new ApiResponseWrapper(response, apiMethod);
                ApiMethodWrapper wrapper = prepareResultApiMethodWrapperInstance(response);
                return (Object) wrapper;

//                return apiMethod;

//                return fetchMappingAdapter(thisMethod).map(mappingAdapter -> {
//                    if (!(invocationResult instanceof ApiMethodWrapper)) {
//                        throw new RuntimeException(String.format("Method %s should return instance of %s class", thisMethod.getName(), ApiMethodWrapper.class.getName()));
//                    }
//
//                    return mappingAdapter.convert(thisMethod).map(abstractApiMethodV2 -> {
//                        ApiOpContext apiOpContext = new ApiOpContext(abstractApiMethodV2);
//
//                        String resolvedPath = resolvePathPatterns(apiOpContext.getApiMethod().getMethodPath(), thisMethod, args);
//                        apiOpContext.getApiMethod().setMethodPath(resolvedPath);
//
//                        Map<String, ?> queryParams = resolveNamedParameters(thisMethod, QueryParam.class, QueryParam::value, args);
//                        apiOpContext.getApiMethod().getRequest().queryParams(queryParams);
//
//                        ApiMethodWrapper apiMethodWrapper = (ApiMethodWrapper) invocationResult;
//                        apiMethodWrapper.prepare(apiOpContext);
//                        Response response = apiOpContext.getApiMethod().callAPI();
//
//                        ApiResponseWrapper responseWrapper = new ApiResponseWrapper(response, abstractApiMethodV2);
//                        ApiMethodWrapper wrapper = prepareResultApiMethodWrapperInstance(responseWrapper);
//                        return (Object) wrapper;
//                    }).orElse(invocationResult);
//                }).orElse(invocationResult);
            }
            return invocationResult;
        };
    }

    private static ApiMethodWrapper prepareResultApiMethodWrapperInstance(Response response) {
        return new ApiMethodWrapper() {
            @Override
            public void prepare(AbstractApiMethodV2 apiMethod) {
                LOGGER.info("Api method has already been called.");
            }

            @Override
            public Response getResult() {
                return response;
            }
        };
    }

    private static void resolvePathPatterns(AbstractApiMethodV2 apiMethod, Method method, Object... values) {
        Map<String, Object> parameterValues = resolveNamedParameters(method, PathVariable.class, PathVariable::value, values);
        parameterValues.forEach((key, value) -> apiMethod.replaceUrlPlaceholder(key, value.toString()));

//        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
//
//        while (matcher.find()) {
//            String foundPattern = matcher.group(1);
//            Object maybeValue = parameterValues.get(foundPattern);
//            if (maybeValue == null) {
//                throw new RuntimeException(String.format("No values found for pattern with name %s", foundPattern));
//            }
//
//            path = matcher.replaceFirst(maybeValue.toString());
//        }
//
//        return path;
    }

    private static <A extends Annotation> Map<String, Object> resolveNamedParameters(Method method, Class<A> annotationClass, Function<A, String> nameGetter, Object... values) {
        Parameter[] parameters = method.getParameters();
        return IntStream.range(0, parameters.length)
                .boxed()
                .filter(index -> parameters[index].isAnnotationPresent(annotationClass))
                .collect(Collectors.toMap(index -> nameGetter.apply(parameters[index].getAnnotation(annotationClass)), index -> values[index]));
    }

    private static boolean isMethodValid(Method method) {
        return !Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())
                && method.isAnnotationPresent(EndpointMapping.class);
    }
}
