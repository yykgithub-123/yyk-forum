package yc.star.forum.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class Article {
    private Long id;

    @ApiModelProperty("关联板块编号")
    private Long boardId;

    @ApiModelProperty("发帖⼈")
    private Long userId;

    @ApiModelProperty("帖⼦标题")
    private String title;

    @ApiModelProperty("访问量")
    private Integer visitCount;

    @ApiModelProperty("回复数")
    private Integer replyCount;

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

    @ApiModelProperty("帖⼦正⽂")
    private String content;

    // 关联系对象-作者
    @ApiModelProperty("⽤⼾信息")
    private User user;

    // 关联系对象-板块
    @ApiModelProperty("板块信息")
    private Board board;

    // ⽤⼾是不是作者
    @ApiModelProperty("⽤⼾是不是作者")
    private Boolean Own;

}