package com.StyleSphere.backend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OtpService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    public void saveOtp(String email ,String otp)
    {
        redisTemplate.opsForValue().set("otp:" + email,otp, Duration.ofMinutes(5));
    }

    public String getOtp(String email) {
        return redisTemplate.opsForValue().get("otp:" + email);
    }

    public void deleteOtp(String email) {
        redisTemplate.delete("otp:" + email);
    }

}
