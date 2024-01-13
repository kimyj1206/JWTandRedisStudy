package project.mapper;

import org.apache.ibatis.annotations.Mapper;
import project.domain.User;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // select all
    List<User> findByAll();

    // select user by email
    Optional<User> findById(String email);

    // insert user = join
    int insertUser(User user);

    // select user = login
    int selectUser(User user);

    // update user
    int updateUser(User user);

    // delete user
    int deleteUser(String email);
}
