package yc.star.forum.dao;

import org.apache.ibatis.annotations.Mapper;
import yc.star.forum.model.Board;

import java.util.List;

@Mapper
public interface BoardMapper {
    int insert(Board row);

    int insertSelective(Board row);

    Board selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Board row);

    int updateByPrimaryKey(Board row);

    // 查询前N个正常状态的版块
    List<Board> selectByNum(Integer num);

    // 获取所有板块列表
    List<Board> selectAllBoards();
}