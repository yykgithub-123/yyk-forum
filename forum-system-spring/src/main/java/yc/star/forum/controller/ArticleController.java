package yc.star.forum.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.Constant;
import yc.star.forum.common.ResultCode;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Article;
import yc.star.forum.model.Board;
import yc.star.forum.model.User;
import yc.star.forum.service.IArticleService;
import yc.star.forum.service.IBoradService;
import yc.star.forum.service.IUserService;
import yc.star.forum.model.PageInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("article")
@Api(tags = "帖子测试接口")
public class ArticleController {
    @Resource
    private IArticleService articleService;
    @Resource
    private IBoradService boradService;
    @Resource
    private IUserService userService;

    @ApiOperation("发帖子")
    @PostMapping("creat")
    public AppResult creatArticle (HttpServletRequest request
            ,@ApiParam(value = "版块Id") @RequestParam(value = "boardId") @NonNull Long boardId
            ,@ApiParam(value = "⽂章标题") @RequestParam(value = "title") @NonNull String title
            ,@ApiParam(value = "帖⼦内容") @RequestParam(value = "content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        // 获取到登录用户
        User user = (User) session.getAttribute(Constant.USER_SESSION);

        // 校验用户是否禁言
        if (user.getState() == 1) {
            log.info("用户被禁言");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BANNED));
        }

        // 校验板块是否有效
        Board board = boradService.selectByPrimaryKey(boardId);
        if (board == null || board.getDeleteState() == 1 || board.getState() == 1) {
            log.info("板块无效 id : " + boardId);
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }

        // 填补参数
        Article article = new Article();
        article.setUserId(user.getId());
        article.setBoardId(boardId);
        article.setTitle(title);
        article.setContent(content);

        // 调用Service
        articleService.create(article);
        return AppResult.success();
    }

    @ApiOperation("根据id获取所有帖子")
    @GetMapping("getAllByBoardId")
    public AppResult<PageInfo<Article>> getAllByBoardId (
            @ApiParam("板块id") @RequestParam(value = "boardId",required = false) Long boardId,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<Article> articles = null;
        if (boardId == null) {
            // 查询所有的帖子
            articles = articleService.selectAll(pageNum, pageSize);
        } else {
            // 根据板块id查询帖子
            articles = articleService.selectByBoardId(boardId, pageNum, pageSize);
        }
        return AppResult.success(articles);
    }

    @ApiOperation("根据id获取帖子详细信息")
    @GetMapping("/details")
    public AppResult<Article> getDetails (@ApiParam("帖子id") @RequestParam("id") @NonNull Long id,
                                          HttpServletRequest request) {
        Article article = articleService.selectDetailById(id);
        if (article == null) {
            log.warn("帖子不存在");
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        // 判断帖子作者是否为当前用户
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        Long ArticleUserId = article.getUserId();
        if (user.getId() == ArticleUserId) {
            article.setOwn(true);
        }
        // 返回文章信息
        return AppResult.success(article);
    }

    @ApiOperation("修改帖子")
    @PostMapping("/modify")
    public AppResult modify (HttpServletRequest request
            ,@ApiParam("帖子id") @RequestParam("id") @NonNull Long id
            ,@ApiParam("帖子标题") @RequestParam("title") @NonNull String title
            ,@ApiParam("帖子正文") @RequestParam("content") @NonNull String content) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 根据帖子id获取用户信息
        Article article = articleService.selectById(id);
        // 校验帖子的状态
        if (article == null) {
            log.warn("帖子不存在");
            return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
        }
        if (article.getState() == 1) {
            log.info("帖子的状态异常");
            return  AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
        }
        // 校验身份是否能编辑
        if (user.getId() != article.getUserId()) {
            log.warn("没有权限，不能编辑");
            return AppResult.failed(ResultCode.FAILED_UNAUTHORIZED);
        }
        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setTitle(title);
        updateArticle.setContent(content);
        // 调用Service
        articleService.modify(updateArticle);
        return AppResult.success();
    }

    @ApiOperation("更新点赞数")
    @GetMapping("/thumbsUp")
    public AppResult thumbsUp (@ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {
        // 调用Service更新点赞数
        articleService.thumbsUpById(id);
        return AppResult.success();
    }

    @ApiOperation("删除帖子")
    @GetMapping("/delete")
    public AppResult deleteById (HttpServletRequest request,
                                 @ApiParam("帖子id") @RequestParam("id") @NonNull Long id) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(Constant.USER_SESSION);
        // 查找出帖子信息
        Article article = articleService.selectById(id);
        // 判断帖子的有效性
        if (article == null || article.getDeleteState() == 1
                || article.getUserId() == null || article.getState() ==1) {
            log.warn("帖子状态无效");
            return AppResult.failed(ResultCode.FAILED_ARTICLE_STATE_ABNORMAL);
        }
        // 判断用户是否有权限删除
        if (user.getId() != article.getUserId()) {
            log.warn("用户没有权限进行删除");
            return AppResult.failed(ResultCode.FAILED_UNAUTHORIZED);
        }
        // 调用Service进行删除
        articleService.deleteById(id);
        return AppResult.success();
    }

    @ApiOperation("获取用户的帖子列表")
    @GetMapping("/getAllByUserId")
    public AppResult<List<Article>> getAllByUserId (HttpServletRequest request
            ,@ApiParam("用户id") @RequestParam(value = "userId",required = false) Long userId) {
        // 校验参数是否为空，如果为空就从session中获取
        if (userId == null) {
            // 从session中获取用户对象
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(Constant.USER_SESSION);
            userId = user.getId();
        }
        // 校验用户是否存在
        User user = userService.selectUserInfo(userId);
        if (user == null || user.getDeleteState() == 1) {
            log.warn("用户不存在");
            throw new ApplicationException(AppResult.failed("该用户不存在或已注销"));
        }
        // 调用业务层查询用户的所有帖子
        List<Article> articles = articleService.selectAllByUserId(userId);
        return AppResult.success(articles);
    }

    @ApiOperation("搜索帖子")
    @GetMapping("/search")
    public AppResult<List<Article>> searchArticles(
            @ApiParam("搜索关键词") @RequestParam(value = "keyword", required = false) String keyword,
            @ApiParam("板块ID") @RequestParam(value = "boardId", required = false) Long boardId) {
        List<Article> articles = articleService.searchArticles(keyword, boardId);
        return AppResult.success(articles);
    }

    @ApiOperation("获取所有帖子列表")
    @GetMapping("/list")
    public AppResult getAllArticles(HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        List<Article> articles = articleService.getAllArticles();
        return AppResult.success(articles);
    }

    @ApiOperation("删除帖子")
    @PostMapping("/delete")
    public AppResult deleteArticle(@ApiParam(value = "帖子ID") @RequestParam("id") @NonNull Long id,
                                 HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        articleService.deleteArticle(id);
        return AppResult.success();
    }

    @ApiOperation("恢复帖子")
    @PostMapping("/restore")
    public AppResult restoreArticle(@ApiParam(value = "帖子ID") @RequestParam("id") @NonNull Long id,
                                  HttpServletRequest request) {
        // 检查是否是管理员
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constant.USER_SESSION) == null) {
            return AppResult.failed(ResultCode.FAILED_LOGIN);
        }
        User currentUser = (User) session.getAttribute(Constant.USER_SESSION);
        if (currentUser.getIsAdmin() != 1) {
            return AppResult.failed(ResultCode.FAILED_PERMISSION);
        }

        articleService.restoreArticle(id);
        return AppResult.success();
    }
}