package yc.star.forum.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import yc.star.forum.model.ArticleReply;

import java.util.List;

@Mapper
public interface ArticleReplyMapper {
    int insert(ArticleReply row);

    int insertSelective(ArticleReply row);

    ArticleReply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ArticleReply row);

    int updateByPrimaryKey(ArticleReply row);

    // 根据帖子id查询所有回复
    List<ArticleReply> selectByArticleId (@Param("articleId") Long articleId);
}