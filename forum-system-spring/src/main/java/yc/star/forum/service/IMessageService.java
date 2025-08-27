package yc.star.forum.service;


import org.springframework.transaction.annotation.Transactional;
import yc.star.forum.model.Message;

import java.util.List;

public interface IMessageService {
    /**
     * 创建站内信
     * @param message
     */
    void create (Message message);

    // 根据id查询站内信
    Message selectByPrimaryKey(Long id);

    // 根据用户id查询所有未读信的数量
    Integer selectUnreadCount(Long receiveUserId);

    // 根据用户id查询所有的站内信
    List<Message> selectByReceiveUserId(Long receiveUserId);

    // 根据站内信id更新消息状态
    void updateStateById(Long id, Byte state);

    // 回复消息
    @Transactional
    void reply (Long repliedId, Message message);
}
