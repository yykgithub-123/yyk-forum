package yc.star.forum.service;


import org.springframework.transaction.annotation.Transactional;
import yc.star.forum.model.ArticleReply;

import java.util.List;

public interface IArticleReplyService {
    // 创建回复帖子信息
    @Transactional
    void create (ArticleReply articleReply);

    // 根据帖子id查询所有回复
    List<ArticleReply> selectByArticleId (Long articleId);
}
