package yc.star.forum.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class Board {
    private Long id;

    @ApiModelProperty("版块名")
    private String name;

    @ApiModelProperty("帖⼦数量")
    private Integer articleCount;

    @ApiModelProperty("排序优先级")
    private Integer sort;

    @ApiModelProperty("状态")
    private Byte state;

    @ApiModelProperty("是否删除")
    private Byte deleteState;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;


}