package yc.star.forum.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;
import yc.star.forum.dao.MessageMapper;
import yc.star.forum.dao.UserMapper;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Message;
import yc.star.forum.model.User;
import yc.star.forum.service.IMessageService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public void create(Message message) {
        // 校验参数
        if (message == null
                || message.getPostUserId() == null || message.getReceiveUserId() == null
                || !StringUtils.hasLength(message.getContent())) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 查出接收者用户信息
        User receiveUser = userMapper.selectByPrimaryKey(message.getReceiveUserId());
        // 校验接收者用户的有效性
        if (receiveUser == null || receiveUser.getDeleteState() == 1) {
            log.warn("该接收用户不存在");
            throw new ApplicationException(AppResult.failed("用户不存在或已注销，发送失败"));
        }
        // 设置参数默认值
        message.setState((byte) 0); // 表示未读状态
        message.setDeleteState((byte) 0);
        Date date = new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);

        // 调用数据层执行插入操作
        int row = messageMapper.insertSelective(message);
        if (row != 1) {
            throw new ApplicationException(AppResult.failed("插入失败，服务器内部错误，请联系管理员"));
        }
    }

    @Override
    public Message selectByPrimaryKey(Long id) {
        if (id == null || id <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message message = messageMapper.selectByPrimaryKey(id);
        if (message == null || message.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed("该消息不存在了"));
        }
        return message;
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        // 校验参数
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用数据层查询结果
        Integer result = messageMapper.selectUnreadCount(receiveUserId);
        if (result < 0) {
            result = 0;
        }
        return result;
    }

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        // 校验参数
        if (receiveUserId == null || receiveUserId <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用数据层查询结果
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);
        return messages;
    }

    @Override
    public void updateStateById(Long id, Byte state) {
        if (id == null || id <= 0 || state < 0 || state > 2) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验消息状态
        Message message = messageMapper.selectByPrimaryKey(id);
        if (message == null || message.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed("该消息不存在了"));
        }
        // 准备数据
        Message updateMessage = new Message();
        updateMessage.setId(id);
        updateMessage.setState(state);

        int row = messageMapper.updateByPrimaryKeySelective(updateMessage);
        if (row != 1) {
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void reply(Long repliedId, Message message) {
        if (repliedId == null || repliedId <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Message existMessage = messageMapper.selectByPrimaryKey(repliedId);
        // 判断这个站内信的状态
        if (existMessage == null || existMessage.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed("该消息不存在了"));
        }
        // 更新该消息的状态
        updateStateById(repliedId, (byte) 2);

        // 将回复的内容写入到数据库
        create(message);
    }
}
