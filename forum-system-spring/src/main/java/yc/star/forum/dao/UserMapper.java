package yc.star.forum.dao;

import org.apache.ibatis.annotations.Mapper;
import yc.star.forum.model.User;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    // 根据用户名查找用户信息
    User selectByUserName(String username);

    // 获取所有用户列表
    List<User> selectAllUsers();
}