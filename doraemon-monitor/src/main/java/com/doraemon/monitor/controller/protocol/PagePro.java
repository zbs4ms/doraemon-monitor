package com.doraemon.monitor.controller.protocol;

import lombok.Data;

/**
 * Created by zbs on 2017/7/7.
 */
@Data
public class PagePro {
    int page=1;
    int row=10;

    private PagePro(){}

    public static PagePro create(Integer page,Integer row){
        if(page==null)
            page=1;
        if(row == null)
            row=10;
        PagePro pagePro = new PagePro();
        page = page > 100 ? 100 : page;
        row = row > 1000 ? 1000 : row;
        pagePro.setPage(page);
        pagePro.setRow(row);
        return pagePro;
    }
}
