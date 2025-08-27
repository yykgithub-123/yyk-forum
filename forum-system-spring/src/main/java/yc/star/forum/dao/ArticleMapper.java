package yc.star.forum.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import yc.star.forum.model.Article;

import java.util.List;

@Mapper
public interface ArticleMapper {
    int insert(Article row);

    int insertSelective(Article row);

    Article selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKeyWithBLOBs(Article row);

    int updateByPrimaryKey(Article row);

    // 查询所有帖子
    List<Article> selectAll();

    // 根据板块id查询帖子
    List<Article> selectByBoardId(@Param("boardId") Long boardId);

    // 根据id查询帖子详细信息
    Article selectDetailById (@Param("id") Long id);

    // 根据作者id查询所有帖子
    List<Article> selectAllByUserId (@Param("userId") Long userId);

    // 添加搜索方法
    List<Article> searchArticles(@Param("keyword") String keyword, 
                               @Param("boardId") Long boardId);

    // 查询总记录数
    int selectCount(Object param);

    // 查询指定板块下的记录数
    int selectCountByBoardId(Long boardId);

    // 分页查询所有帖子
    List<Article> selectAllWithPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    // 分页查询指定板块下的帖子
    List<Article> selectByBoardIdWithPage(@Param("boardId") Long boardId, @Param("offset") int offset, @Param("pageSize") int pageSize);

    // 获取所有帖子列表(包括已删除的)
    List<Article> selectAllArticles();

    // 根据用户id查询帖子列表
    List<Article> selectByUserId(Long userId);

    // 根据用户id分页查询帖子列表
    List<Article> selectByUserIdWithPage(@Param("userId") Long userId,
                                       @Param("offset") Integer offset,
                                       @Param("pageSize") Integer pageSize);
}