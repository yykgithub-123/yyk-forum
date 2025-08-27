package yc.star.forum.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class Message {
    private Long id;

    @ApiModelProperty("发送者")
    private Long postUserId;

    @ApiModelProperty("接收者")
    private Long receiveUserId;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("状态")
    private Byte state;

    @ApiModelProperty("是否删除")
    private Byte deleteState;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    // 关联系对象 - 用户
    @ApiModelProperty("发送者用户信息")
    private User postUser;

}