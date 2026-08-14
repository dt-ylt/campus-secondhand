package com.campus.secondhand.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 *
 * 负责：1)登录成功后生成 token；2)后续请求时解析 token 拿到用户信息、校验是否有效。
 * token 里装着 userId、用户名、过期时间，并用密钥签名防伪造。
 *
 * @Component：交给 Spring 管理，之后在拦截器里可以用 @Autowired 注入使用。
 */
@Slf4j
@Component
public class JwtUtils {

    /** 从 application.yml 读 jwt.secret（密钥，签名用） */
    @Value("${jwt.secret}")
    private String secret;

    /** 从 application.yml 读 jwt.expiration（token 有效期，毫秒） */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 用密钥字符串生成一个签名用的 SecretKey 对象
     * hmacShaKeyFor 要求密钥至少 32 字节，我们的密钥够长
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 token
     * 把 userId 放进 subject，用户名放进自定义 claim，设上签发时间和过期时间，最后签名
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // 主体：用户ID
                .claim("username", username)          // 自定义字段：用户名
                .setIssuedAt(now)                     // 签发时间
                .setExpiration(expiryDate)            // 过期时间
                .signWith(getSigningKey())            // 用密钥签名(防伪造)
                .compact();                           // 拼成 xxxxx.yyyyy.zzzzz 形式
    }

    /**
     * 解析 token，拿到载荷(Claims)。token 无效或过期时会抛异常
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())       // 用同一个密钥验签
                .build()
                .parseClaimsJws(token)                // 验签 + 解析，验不过会抛异常
                .getBody();                           // 取出载荷
    }

    /**
     * 从 token 取用户ID（阶段3的拦截器会用它知道"当前是谁"）
     */
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    /**
     * 从 token 取用户名
     */
    public String getUsernameFromToken(String token) {
        return parseToken(token).get("username", String.class);
    }

    /**
     * 校验 token 是否有效（能正常解析且没过期）
     * 有效返回 true，无效/过期返回 false
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.warn("token 无效：{}", e.getMessage());
            return false;
        }
    }
}
