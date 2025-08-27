package yc.star.forum.service;

import yc.star.forum.model.Board;

import java.util.List;

public interface IBoradService {

    // 查询前N条板块的信息
    List<Board> selectByNum(Integer num);

    // 获取所有板块
    List<Board> selectAll();

    // 根据id获取板块信息
    Board selectById(Long id);

    // 板块下的帖子数加1
    void addOneArticleCountById(Long id);

    // 根据id查找板块
    Board selectByPrimaryKey(Long id);

    // 板块下的帖子数加-1
    void subOneArticleCountById(Long id);

    // 创建板块
    void createBoard(Board board);

    // 删除板块
    void deleteBoard(Long id);

}
