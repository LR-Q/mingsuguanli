package com.yxly;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encoded = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + encoded);
        
        // 验证密码
        boolean matches = encoder.matches(password, encoded);
        System.out.println("密码验证: " + matches);
        
        // 验证现有的哈希值
        String existingHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIh9TcOKYHKu6P5wHKxNKYHNka";
        boolean existingMatches = encoder.matches(password, existingHash);
        System.out.println("现有哈希验证: " + existingMatches);
    }
}
