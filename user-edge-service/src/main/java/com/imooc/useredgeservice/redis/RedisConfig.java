package com.imooc.useredgeservice.redis;

import com.imooc.useredgeservice.dto.UserDTO;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author yore
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {


    @Bean
    public RedisTemplate<String, UserDTO> redisDepartmentTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, UserDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(UserDTO.class));
        return template;
    }

//    @Bean
//    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, String> template = new RedisTemplate<>();
//        template.setConnectionFactory(factory);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        return template;
//    }

//    @Bean
//    public RedisTemplate<Object, Object> redisObjTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
//        template.setConnectionFactory(factory);
//        template.setKeySerializer(new RedisObjectSerializer());
//        template.setValueSerializer(new RedisObjectSerializer());
//        return template;
//    }

}

