package yc.star.forum.model;

import lombok.Data;
import java.util.List;

@Data
public class PageInfo<T> {
    private int pageNum;    // 当前页码
    private int pageSize;   // 每页数量
    private int total;      // 总记录数
    private int pages;      // 总页数
    private List<T> list;   // 当前页数据

    public PageInfo(List<T> list, int pageNum, int pageSize, int total) {
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = (total + pageSize - 1) / pageSize;
    }
} 