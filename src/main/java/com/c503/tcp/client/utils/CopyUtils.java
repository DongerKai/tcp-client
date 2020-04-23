package com.c503.tcp.client.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author DongerKai
 * @since 2020/4/23 13:09 ，1.0
 **/
@NoArgsConstructor
public class CopyUtils {
    public static <T,S> List<T> copy(List<S> source, Class<T> targetType){
        if(CollectionUtils.isEmpty(source))
            return Collections.emptyList();
        return source.stream().map(s->{
            T t = BeanUtils.instantiateClass(targetType);
            BeanUtils.copyProperties(s,t);
            return t;
        }).collect(Collectors.toList());
    }

    public static <T,S> T copy(S source, Class<T> target){
        if (source == null)
            return null;
        T t = BeanUtils.instantiateClass(target);
        BeanUtils.copyProperties(source, t);
        return t;
    }
}
