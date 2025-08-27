package yc.star.forum.service;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;
import yc.star.forum.model.Article;
import yc.star.forum.model.PageInfo;

import java.util.List;

public interface IArticleService {
    // 新增帖子
    @Transactional
    void create(Article article);

    // 查询所有帖子（分页）
    PageInfo<Article> selectAll(int pageNum, int pageSize);

    // 根据板块id查询帖子（分页）
    PageInfo<Article> selectByBoardId(Long boardId, int pageNum, int pageSize);

    // 根据id查询帖子详细信息
    Article selectDetailById(Long id);

    // 修改帖子信息
    void modify(Article article);

    // 根据id查询帖子信息
    Article selectById(Long id);

    // 更新帖子点赞数
    void thumbsUpById(Long id);

    // 根据id删除帖子
    @Transactional // 事务操作，其中有一项执行失败全部回退
    void deleteById(Long id);

    // 帖子下的回复数加一
    void addOneArticleReplyById(Long id);

    // 根据作者id查询所有帖子
    List<Article> selectAllByUserId(Long userId);

    // 搜索文章
    List<Article> searchArticles(String keyword, Long boardId);

    // 获取所有帖子列表(包括已删除的)
    List<Article> getAllArticles();

    // 删除帖子
    void deleteArticle(Long id);

    // 恢复帖子
    void restoreArticle(Long id);
}
