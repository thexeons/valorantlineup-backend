package org.gtf.valorantlineup.repositories.redis;

import org.gtf.valorantlineup.models.redis.RedisRefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRefreshTokenRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String KEY = "USER";
    private static final String TOKEN = "TOKEN";

    public boolean saveToken(RedisRefreshToken token) {
        try {
            redisTemplate.opsForHash().put(KEY, token.getUserId(), token);
            redisTemplate.opsForHash().put(TOKEN, token.getToken(), token);
            redisTemplate.expireAt( KEY, token.getExpiryDate());
            redisTemplate.expireAt( TOKEN, token.getExpiryDate());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public RedisRefreshToken fetchTokenByUserId(String userId) {
        RedisRefreshToken user = (RedisRefreshToken) redisTemplate.opsForHash().get(KEY,userId);
        return user;
    }

    public RedisRefreshToken fetchTokenByToken(String refreshToken) {
        RedisRefreshToken token;
        token = (RedisRefreshToken) redisTemplate.opsForHash().get(TOKEN,refreshToken);
        return token;
    }

    public boolean updateToken(RedisRefreshToken token) {
        try {
            redisTemplate.opsForHash().put(KEY, token.getUserId(), token);
            redisTemplate.opsForHash().put(TOKEN, token.getToken(), token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteToken(String userId) {
        try {
            RedisRefreshToken user = (RedisRefreshToken) redisTemplate.opsForHash().get(KEY,userId);
            if(user!=null) {
                redisTemplate.opsForHash().delete(TOKEN, user.getToken());
                redisTemplate.opsForHash().delete(KEY, userId);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
