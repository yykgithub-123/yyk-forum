package yc.star.forum.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import yc.star.forum.common.AppResult;
import yc.star.forum.common.Constant;
import yc.star.forum.common.ResultCode;
import yc.star.forum.model.Board;
import yc.star.forum.model.User;
import yc.star.forum.service.IBoradService;
import yc.star.forum.service.impl.BoardServiceImpl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "板块接口")
@RestController
@Slf4j
@RequestMapping("/board")
public class BoardController {
    @Value("${forum.index.board-num}")
    private Integer num;
    @Autowired
    private BoardServiceImpl boardService;
    @Resource
    private IBoradService boradService;

    @ApiOperation("获取首页板块列表")
    @GetMapping("/topList")
    public AppResult<List<Board>> topList () {
        log.info("读取板块个数num = " + num);
        // 调用service来查询
        List<Board> boards = boardService.selectByNum(num);
        if (boards == null) {
            return AppResult.success(new ArrayList<>());
        }
        return AppResult.success(boards);
    }

    @ApiOperation("根据ID获取板块信息")
    @GetMapping("/getBoardById")
    public AppResult<Board> getBoardById (@ApiParam("板块id") @RequestParam("id") @NonNull Long id) {
        Board board = boardService.selectByPrimaryKey(id);
        // 校验有效性
        if (board == null || board.getDeleteState() == 1) {
            log.warn("该板块无效，id："+id);
            return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
        }
        return AppResult.success(board);
    }

    @ApiOperation("获取板块列表")
    @GetMapping("/list")
    public AppResult list() {
        List<Board> boards = boradService.selectAll();
        return AppResult.success(boards);
    }

    @ApiOperation("添加板块")
    @PostMapping("/add")
    public AppResult addBoard(@ApiParam(value = "板块名称") @RequestParam("name") @NonNull String name,
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

        Board board = new Board();
        board.setName(name);
        boradService.createBoard(board);
        return AppResult.success();
    }

    @ApiOperation("删除板块")
    @PostMapping("/delete")
    public AppResult deleteBoard(@ApiParam(value = "板块ID") @RequestParam("id") @NonNull Long id,
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

        boradService.deleteBoard(id);
        return AppResult.success();
    }
}
