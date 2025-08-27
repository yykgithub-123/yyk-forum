package yc.star.forum.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.ResultCode;
import yc.star.forum.dao.BoardMapper;
import yc.star.forum.exception.ApplicationException;
import yc.star.forum.model.Board;
import yc.star.forum.service.IBoradService;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BoardServiceImpl implements IBoradService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public Board selectByPrimaryKey(Long id) {
        if (id == null) {
            log.error(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出参数校验失败异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn("板块不存在，id ： " + id);
        }
        return board;
    }

    @Override
    public void subOneArticleCountById(Long id) {
        // 参数校验
        if (id == null) {
            // 板块更新帖子数量失败
            log.warn(ResultCode.FAILED_BOARD_ALTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ALTICLE_COUNT));
        }
        // 查询对应的板块
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn("板块不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新帖子数量
        Board updateBoard = new Board();
        updateBoard.setId(id);
        updateBoard.setArticleCount(board.getArticleCount()-1);
        // 如果板块下的帖子数为零，将帖子数设置为零
        if (updateBoard.getArticleCount() < 0) {
            updateBoard.setArticleCount(0);
        }
        // 数据库操作板块帖子数量加1
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            log.error("受影响行数：" + row);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void addOneArticleCountById(Long id) {
        // 参数校验
        if (id == null) {
            // 板块更新帖子数量失败
            log.warn(ResultCode.FAILED_BOARD_ALTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ALTICLE_COUNT));
        }
        // 查询对应的板块
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn("板块不存在");
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        // 更新帖子数量
        Board updateBoard = new Board();
        updateBoard.setId(id);
        updateBoard.setArticleCount(board.getArticleCount()+1);
        // 数据库操作板块帖子数量加1
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        if (row != 1) {
            log.error("受影响行数：" + row);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public List<Board> selectByNum(Integer num) {
        // 非空校验
        if (num <= 0) {
            log.error(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            // 抛出参数校验失败异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO查询数据
        List<Board> boards = boardMapper.selectByNum(num);
        return boards;
    }

    @Override
    public List<Board> selectAll() {
        return boardMapper.selectAllBoards();
    }

    @Override
    public Board selectById(Long id) {
        if (id == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        
        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        return board;
    }

    @Override
    public void createBoard(Board board) {
        if (board == null || board.getName() == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        // 设置默认值
        board.setArticleCount(0);
        board.setSort(0);
        board.setState((byte) 0);
        board.setDeleteState((byte) 0);
        Date date = new Date();
        board.setCreateTime(date);
        board.setUpdateTime(date);

        int row = boardMapper.insertSelective(board);
        if (row != 1) {
            log.error("创建板块失败");
            throw new ApplicationException(AppResult.failed("创建板块失败"));
        }
        log.info("创建板块成功");
    }

    @Override
    public void deleteBoard(Long id) {
        if (id == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }

        Board board = boardMapper.selectByPrimaryKey(id);
        if (board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }

        // 设置删除状态为已删除(1)
        board.setDeleteState((byte) 1);
        board.setUpdateTime(new Date());

        int row = boardMapper.updateByPrimaryKeySelective(board);
        if (row != 1) {
            log.error("删除板块失败");
            throw new ApplicationException(AppResult.failed("删除板块失败"));
        }
        log.info("删除板块成功");
    }
}
