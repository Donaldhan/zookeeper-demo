package org.donald.zookeeper.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;


/**
 * @ClassName: AuthPasswordUtils
 * @Description: 模拟Zookeeper生成客户端加密
 * @see org.donald.zookeeper.auth.AuthPasswordUtilsTest {@link #generateDigest(String)}
 * @Author: Donaldhan
 * @Date: 2018-05-13 12:50
 */
@Slf4j
public class AuthPasswordUtils {
    /**
     * 生成路径的ACL验证方式的密码，加密密文
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateDigest(String password) throws NoSuchAlgorithmException {
        String encodePassword =  DigestAuthenticationProvider.generateDigest(password);
        log.info("{} encoded value:{}",password,encodePassword);
        return encodePassword;
      }
    }
