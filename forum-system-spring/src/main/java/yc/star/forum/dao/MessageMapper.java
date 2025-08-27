package yc.star.forum.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import yc.star.forum.model.Message;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    // 根据用户id查询所有未读信的数量
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    // 根据用户id查询所有的站内信
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}