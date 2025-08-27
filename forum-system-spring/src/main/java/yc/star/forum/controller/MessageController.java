package yc.star.forum.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.Constant;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Message;
import yc.star.forum.model.User;
import yc.star.forum.service.IMessageService;
import yc.star.forum.service.IUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Api(tags = "站内信接⼝")
@RestController
@RequestMapping("message")
public class MessageController {

    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;

    /**
     * 发送站内信
     * @return
     */
    @ApiOperation("创建站内信")
    @PostMapping("/send")
    public AppResult send (HttpServletRequest request
            ,@ApiParam("接收者用户id") @RequestParam("receiveUserId") @NonNull Long receiveUserId
            ,@ApiParam("站内信内容") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        if (user.getState() != 0) {
            return AppResult.failed("你已被禁言");
        }
        // 判断是否给自己发送
        if (user.getId() == receiveUserId) {
            return AppResult.failed("不能给自己发送消息");
        }
        // 判断接收者用户是否有效
        User receiveUser = userService.selectUserInfo(receiveUserId);
        if (receiveUser == null || receiveUser.getDeleteState() == 1) {
            return AppResult.failed("该用户不存在");
        }
        // 准备数据
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(receiveUserId);
        message.setContent(content);
        // 调用业务层插入站内信
        messageService.create(message);
        return AppResult.success();
    }

    /**
     * 获取未读消息个数
     * @param request
     * @return
     */
    @ApiOperation("获取未读消息个数")
    @GetMapping("/getUnreadCount")
    public AppResult<Integer> getUnreadCount (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        Integer result = messageService.selectUnreadCount(user.getId());
        return AppResult.success(result);
    }

    @ApiOperation("查询所有的站内信")
    @GetMapping("/getAll")
    public AppResult<List<Message>> getAll (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        List<Message> messages = messageService.selectByReceiveUserId(user.getId());
        return AppResult.success(messages);
    }

    @ApiOperation("将消息状态改为已读")
    @PostMapping("/markRead")
    public AppResult markRead (HttpServletRequest request
            ,@ApiParam("消息id") @RequestParam("id") @NonNull Long id) {
        Message message = messageService.selectByPrimaryKey(id);
        if (message == null || message.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed("该消息不存在了"));
        }
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 判断消息是不是自己的
        if (user.getId() != message.getReceiveUserId()) {
            return AppResult.failed("该消息不是你的");
        }
        messageService.updateStateById(id, (byte) 1);
        return AppResult.success();
    }

    /**
     * 回复消息
     * @return
     */
    @ApiOperation("回复消息")
    @PostMapping("/reply")
    public AppResult reply (HttpServletRequest request
            ,@ApiParam("要回复消息的id") @RequestParam("repliedId") @NonNull Long repliedId
            ,@ApiParam("回复的内容") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 判断用户的状态
        if (user.getState() == 1) {
            return AppResult.failed("你已被禁言不能发送消息");
        }
        // 判断该消息的状态
        Message existMessage = messageService.selectByPrimaryKey(repliedId);
        if (existMessage == null || existMessage.getDeleteState() == 1) {
            return AppResult.failed("该消息不存在了");
        }
        // 判断该消息是不是自己的
        if (user.getId() != existMessage.getReceiveUserId()) {
            return AppResult.failed("该消息不是你的");
        }
        // 构造消息
        Message message = new Message();
        message.setPostUserId(user.getId());
        message.setReceiveUserId(existMessage.getPostUserId());
        message.setContent(content);
        // 创建消息
        messageService.reply(repliedId,message);
        return AppResult.success();
    }

}
