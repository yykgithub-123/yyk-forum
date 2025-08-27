package yc.star.forum.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;
import yc.star.forum.dao.ArticleReplyMapper;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Article;
import yc.star.forum.model.ArticleReply;
import yc.star.forum.service.IArticleReplyService;
import yc.star.forum.service.IArticleService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleReplyServiceImpl implements IArticleReplyService {
    @Resource
    private ArticleReplyMapper articleReplyMapper;
    @Resource
    private IArticleService articleService;

    @Override
    public void create(ArticleReply articleReply) {
        // 校验参数合格性
        if (articleReply == null
                || articleReply.getArticleId() == null
                || articleReply.getPostUserId() == null
                || articleReply.getContent() == null) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        Article article = articleService.selectById(articleReply.getArticleId());
        // 校验帖子的有效性
        if (article == null || article.getDeleteState() == 1) {
            log.warn("帖子不存在或已删除");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        if (article.getState() == 1) {
            log.info("帖子已被警用，不能回复");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        // 准备数据设置默认值
        articleReply.setState((byte) 0); // 状态
        articleReply.setDeleteState((byte) 0); // 是否删除
        articleReply.setLikeCount(0); // 点赞数量
        // 时间
        Date date = new Date();
        articleReply.setCreateTime(date); // 创建时间
        articleReply.setUpdateTime(date); // 更新时间

        // 调用DAO来执行创建回复操作
        int row = articleReplyMapper.insertSelective(articleReply);
        if (row != 1) {
            log.warn("访问次数更新失败");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        // 更新帖子的回复数
        articleService.addOneArticleReplyById(articleReply.getArticleId());
    }

    @Override
    public List<ArticleReply> selectByArticleId(Long articleId) {
        // 校验参数
        if (articleId == null || articleId <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<ArticleReply> articleReplies = articleReplyMapper.selectByArticleId(articleId);
        return articleReplies;
    }
}
