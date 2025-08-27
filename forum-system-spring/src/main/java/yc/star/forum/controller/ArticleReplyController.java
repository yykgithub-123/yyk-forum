package yc.star.forum.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.Constant;
import yc.star.forum.model.Article;
import yc.star.forum.model.ArticleReply;
import yc.star.forum.model.User;
import yc.star.forum.service.IArticleReplyService;
import yc.star.forum.service.IArticleService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reply")
@Api(tags = "回复帖子接口")
public class ArticleReplyController {
    @Resource
    private IArticleReplyService articleReplyService;
    @Resource
    private IArticleService articleService;

    @ApiOperation("创建回复")
    @PostMapping("/create")
    public AppResult create(HttpServletRequest request
            , @ApiParam("关联帖子id") @RequestParam("articleId") @NonNull Long articleId
            , @ApiParam("回复内容") @RequestParam("content") @NonNull String content) {
        // 获取用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 判断用户是否为禁言状态
        if (user.getState() == 1) {
            log.info("用户已被禁言，不能回帖");
            AppResult.failed("用户已被禁言，不能回帖");
        }
        // 校验帖子的有效性
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            log.warn("帖子不存在，或者已被删除");
            return AppResult.failed("帖子不存在，或者已被删除");
        }
        if (article.getState() == 1) {
            log.info("帖子被禁用");
            return AppResult.failed("该帖子状态异常，已被禁用，禁止回复");
        }
        // 构造数据调用业务层
        ArticleReply articleReply = new ArticleReply();
        articleReply.setArticleId(articleId);
        articleReply.setPostUserId(user.getId());
        articleReply.setContent(content);
        articleReplyService.create(articleReply);
        // 回复成功
        return AppResult.success("回复成功");
    }

    @ApiOperation("查询帖子下的所有回复")
    @GetMapping("/getReplies")
    public AppResult<List<ArticleReply>> getRepliesByArticleId (
            @ApiParam("帖子id") @RequestParam("articleId") @NonNull Long articleId) {
        // 校验帖子的有效性
        Article article = articleService.selectById(articleId);
        if (article == null || article.getDeleteState() == 1) {
            log.warn("帖子已被删除或不存在");
            return AppResult.failed("帖子已被删除或不存在");
        }
        if (article.getState() == 1) {
            log.info("帖子已被禁用不能回复");
            return AppResult.failed("帖子已被禁用不能回复");
        }
        // 调用业务层查询所有回复
        List<ArticleReply> articleReplies = articleReplyService.selectByArticleId(articleId);
        return AppResult.success(articleReplies);
    }
}