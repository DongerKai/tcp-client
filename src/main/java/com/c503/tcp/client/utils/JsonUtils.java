package com.c503.tcp.client.utils;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 12:53 ，1.0
 **/
@Slf4j
@NoArgsConstructor
public class JsonUtils {
    public static final ObjectMapper mapper = new ObjectMapper();
    public static final ObjectMapper serializeMapper = new ObjectMapper();

    static {
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mapper.setDefaultPropertyInclusion(NON_NULL);
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//可以解决对象内容比json字符串内容少时转译报错问题

        serializeMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serializeMapper.registerModule(new JavaTimeModule());
        serializeMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        serializeMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    public static <T> T readValue(String jsonString, Class<T> clazz) {
        return FunctionUtils.applyThrowException(()->mapper.readValue(jsonString, clazz));
    }

    //FunctionUtils.applyThrowException(()->CastUtils.cast(mapper.readValue(jsonString, typeRef)));
    public static <T> T readValue(String jsonString, TypeReference typeRef) {
        return FunctionUtils.applyThrowException(()->mapper.readValue(jsonString, typeRef));
    }

    public static <T> T readValueIgnoreException(String jsonString, Class<T> clazz) {
        return FunctionUtils.applyIgnoreException(()->mapper.readValue(jsonString, clazz));
    }

    public static String writeValueAsString(Object obj) {
        return FunctionUtils.applyThrowException(()->mapper.writeValueAsString(obj));
    }

    public static <T> T readValue(String jsonString, Class<T> clazz,RuntimeException exception) {
        return FunctionUtils.applyThrowException(()->mapper.readValue(jsonString, clazz),exception);
    }

    //return FunctionUtils.applyThrowException(()->CastUtils.cast(mapper.readValue(jsonString, typeRef)),exception);
    public static <T> T readValue(String jsonString, TypeReference typeRef,RuntimeException exception) {
        return FunctionUtils.applyThrowException(()->mapper.readValue(jsonString, typeRef),exception);
    }

    public static String writeValueAsString(Object obj,RuntimeException exception) {
        return FunctionUtils.applyThrowException(()->mapper.writeValueAsString(obj),exception);
    }

    public static <T> List<T> readJsonArray(String jsonString, Class<T> clazz){
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        return FunctionUtils.applyThrowException(()->mapper.readValue(jsonString, listType));
    }
}
