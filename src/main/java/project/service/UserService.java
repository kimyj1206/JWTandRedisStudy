package project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.domain.User;
import project.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public List<User> findAll() {
        return userMapper.findByAll();
    }

    public Optional<User> findById(String email) {
        return userMapper.findById(email);
    }

    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    public int selectUser(User user) {
        return userMapper.selectUser(user);
    }

    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    public int deleteUser(String email) {
        return userMapper.deleteUser(email);
    }
}
