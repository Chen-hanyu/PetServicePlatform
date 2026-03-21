package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.User;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
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
    public User createUserByPhone(String phone) {
        User user = new User();
        user.setRole("USER");
        user.setPhone(phone);
        user.setNickname(buildDefaultNickname(phone));
        user.setStatus("ACTIVE");
        userMapper.insert(user);
        return userMapper.selectById(user.getId());
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
