package yc.star.forum.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class ArticleReply {
    private Long id;

    @ApiModelProperty("关联帖⼦编号")
    private Long articleId;

    @ApiModelProperty("楼主⽤⼾")
    private Long postUserId;

    @ApiModelProperty("回复编号")
    private Long replyId;

    @ApiModelProperty("楼主下的回复⽤⼾编号")
    private Long replyUserId;

    @ApiModelProperty("回贴内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("状态")
    private Byte state;

    @ApiModelProperty("是否删除")
    private Byte deleteState;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    // 关联系对象-用户
    @ApiModelProperty("用户信息")
    private User user;

}