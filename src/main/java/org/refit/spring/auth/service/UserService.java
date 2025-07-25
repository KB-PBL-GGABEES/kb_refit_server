package org.refit.spring.auth.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void updatePoint(User user, Long carbon, Long star) {
        user.setTotalCarbonPoint(user.getTotalCarbonPoint() + carbon);
        user.setTotalStarPoint(user.getTotalStarPoint() + star);
        userMapper.updateReward(carbon, star);
    }
}
