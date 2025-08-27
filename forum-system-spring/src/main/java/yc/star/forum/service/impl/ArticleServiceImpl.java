package yc.star.forum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;
import yc.star.forum.dao.ArticleMapper;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Article;
import yc.star.forum.model.Board;
import yc.star.forum.model.User;
import yc.star.forum.model.PageInfo;
import yc.star.forum.service.IArticleService;
import yc.star.forum.service.IBoradService;
import yc.star.forum.service.IUserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleServiceImpl implements IArticleService {
    @Resource
    private ArticleMapper articleMapper;

    // 用户和板块的操作
    @Resource
    private IBoradService boardService;
    @Resource
    private IUserService userService;

    @Override
    public PageInfo<Article> selectAll(int pageNum, int pageSize) {
        // 获取总记录数
        int total = articleMapper.selectCount(null);
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询当前页数据
        List<Article> articles = articleMapper.selectAllWithPage(offset, pageSize);
        
        // 返回分页信息
        return new PageInfo<>(articles, pageNum, pageSize, total);
    }

    @Override
    public PageInfo<Article> selectByBoardId(Long boardId, int pageNum, int pageSize) {
        if (boardId == null || boardId <= 0) {
            log.warn("板块id无效，id ："+boardId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 校验板块是否有效
        Board board = boardService.selectByPrimaryKey(boardId);
        if (board == null) {
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        
        // 获取该板块下的总记录数
        int total = articleMapper.selectCountByBoardId(boardId);
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询当前页数据
        List<Article> articles = articleMapper.selectByBoardIdWithPage(boardId, offset, pageSize);
        
        // 返回分页信息
        return new PageInfo<>(articles, pageNum, pageSize, total);
    }

    @Override
    public Article selectDetailById(Long id) {
        // 校验参数
        if (id == null || id <= 0) {
            log.warn("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO查询帖子相关信息
        Article article = articleMapper.selectDetailById(id);
        // 校验帖子状态
        if (article == null) {
            log.warn("帖子不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        // 更新帖子的访问数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount()+1);
        // 保存到数据库
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn("访问次数更新失败");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        article.setVisitCount(article.getVisitCount()+1);
        return article;
    }

    @Override
    public void modify(Article article) {
        if (article.getId() == null
                || !StringUtils.hasLength(article.getTitle())
                || !StringUtils.hasLength(article.getContent())) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        // 设置参数
        article.setUpdateTime(new Date());
        // 调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(article);
        if (row != 1) {
            log.warn("帖子更新sql异常");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public Article selectById(Long id) {
        if (id == null || id <= 0) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        Article article = articleMapper.selectByPrimaryKey(id);
        return article;
    }

    @Override
    public void thumbsUpById(Long id) {
        // 校验帖子状态
        if (id == null || id <= 0) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        Article article = selectById(id);
        if (article == null || article.getDeleteState() == 1 || article.getState() == 1) {
            log.info("帖子状态异常，不能点赞");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        // 准备数据
        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setLikeCount(article.getLikeCount()+1);
        // 调用DAO更新帖子点赞数
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn("更新sql出问题，请检查");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        log.info("帖子点赞数更新成功");
    }

    @Override
    public void deleteById(Long id) {
        // 校验参数
        if (id == null || id <= 0) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        Article article = articleMapper.selectByPrimaryKey(id);
        // 校验帖子状态
        if (article == null || article.getDeleteState() == 1
                || article.getUserId() == null
                || article.getBoardId() == null) {
            log.warn("帖子状态异常，删除失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_STATE_ABNORMAL));
        }
        // 准备数据
        Article deleteArticle = new Article();
        deleteArticle.setId(article.getId());
        deleteArticle.setDeleteState((byte) 1);
        // 调用DAO执行删除操作
        int row = articleMapper.updateByPrimaryKeySelective(deleteArticle);
        if (row != 1) {
            log.error("删除sql异常，请检查sql语句");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        // 处理用户和板块下的帖子数
        boardService.subOneArticleCountById(article.getBoardId());
        userService.subOneArticleCountById(article.getUserId());
        log.info("帖子删除成功");
    }

    @Override
    public void addOneArticleReplyById(Long id) {
        // 校验帖子状态
        if (id == null || id <= 0) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        Article article = selectById(id);
        if (article == null || article.getDeleteState() == 1 || article.getState() == 1) {
            log.info("帖子状态异常，不能回复");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        // 准备数据
        Article updateArticle = new Article();
        updateArticle.setId(id);
        updateArticle.setReplyCount(article.getReplyCount()+1);
        // 调用DAO，帖子回复数加一
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if (row != 1) {
            log.warn("更新sql出问题，请检查");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        log.info("回复成功");
    }

    @Override
    public List<Article> selectAllByUserId(Long userId) {
        // 校验参数的合格性
        if (userId == null || userId <= 0) {
            log.info("参数校验失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_NOT_EXISTS));
        }
        // 校验用户的有效性
        User user = userService.selectUserInfo(userId);
        if (user == null || user.getDeleteState() == 1) {
            log.warn("用户不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        List<Article> articles = articleMapper.selectAllByUserId(userId);
        // 如果没有查出元素
        if (articles.isEmpty()) {
            articles = new ArrayList<>();
        }
        return articles;
    }

    @Override
    public void create(Article article) {
        if (article == null || article.getBoardId() == null
                || article.getUserId() == null
                || !StringUtils.hasLength(article.getTitle())
                || !StringUtils.hasLength(article.getContent())) {
            // 参数校验失败
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE.toString()));
        }
        // 设置默认值
        article.setVisitCount(0); // 访问数
        article.setReplyCount(0); // 回复数
        article.setLikeCount(0); // 点赞数
        article.setDeleteState((byte) 0);
        article.setState((byte) 0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);
        // 写入数据库
        int row = articleMapper.insertSelective(article);
        if (row != 1) {
            log.error("文章新增失败");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        // 用户发帖数加1
        User user = userService.selectUserInfo(article.getUserId());
        if (user == null) {
            log.warn("发帖失败，没有找到指定用户 ： " + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        userService.addOneArticleCountById(article.getUserId());
        // 板块帖子数加1
        Board board = boardService.selectByPrimaryKey(article.getBoardId());
        if (board == null) {
            log.warn("发帖失败，没有找到指定板块 ： " + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        boardService.addOneArticleCountById(article.getBoardId());

        log.info("发帖成功");
    }

    @Override
    public List<Article> searchArticles(String keyword, Long boardId) {
        // 如果板块id不为空，验证板块是否有效
        if (boardId != null) {
            Board board = boardService.selectByPrimaryKey(boardId);
            if (board == null || board.getDeleteState() == 1 || board.getState() == 1) {
                log.warn("板块无效 id : " + boardId);
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_BANNED));
            }
        }
        
        // 调用mapper执行搜索
        List<Article> articles = articleMapper.searchArticles(keyword, boardId);
        return articles != null ? articles : new ArrayList<>();
    }

    @Override
    public List<Article> getAllArticles() {
        return articleMapper.selectAllArticles();
    }

    @Override
    public void deleteArticle(Long id) {
        if (id == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        Article article = articleMapper.selectByPrimaryKey(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }

        // 设置删除状态为已删除(1)
        article.setDeleteState((byte) 1);
        article.setUpdateTime(new Date());

        int row = articleMapper.updateByPrimaryKeySelective(article);
        if (row != 1) {
            log.error("删除帖子失败");
            throw new ApplicationException(AppResult.failed("删除帖子失败"));
        }
        log.info("删除帖子成功");

        // 更新用户和板块的帖子数量
        userService.subOneArticleCountById(article.getUserId());
        boardService.subOneArticleCountById(article.getBoardId());
    }

    @Override
    public void restoreArticle(Long id) {
        if (id == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        Article article = articleMapper.selectByPrimaryKey(id);
        if (article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }

        // 设置删除状态为正常(0)
        article.setDeleteState((byte) 0);
        article.setUpdateTime(new Date());

        int row = articleMapper.updateByPrimaryKeySelective(article);
        if (row != 1) {
            log.error("恢复帖子失败");
            throw new ApplicationException(AppResult.failed("恢复帖子失败"));
        }
        log.info("恢复帖子成功");

        // 更新用户和板块的帖子数量
        userService.addOneArticleCountById(article.getUserId());
        boardService.addOneArticleCountById(article.getBoardId());
    }
}