package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.User;
import com.petplatform.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
    }

    public User getByIdOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Transactional
    public User createUser(String phone, String rawPassword, String nickname) {
        if (findByPhone(phone) != null) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "Phone already registered");
        }
        User user = new User();
        user.setRole("USER");
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setNickname((nickname == null || nickname.isBlank()) ? buildDefaultNickname(phone) : nickname.trim());
        user.setStatus("ACTIVE");
        userMapper.insert(user);
        return userMapper.selectById(user.getId());
    }

    public boolean matchesPassword(User user, String rawPassword) {
        return user.getPasswordHash() != null && passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    public void ensureActive(User user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
    }

    public UserProfileResponse toUserProfile(User user) {
        return UserProfileResponse.from(user);
    }

    private String buildDefaultNickname(String phone) {
        String suffix = phone.length() >= 4 ? phone.substring(phone.length() - 4) : phone;
        return "用户" + suffix;
    }
}
