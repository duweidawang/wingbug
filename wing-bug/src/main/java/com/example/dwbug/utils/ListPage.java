package com.example.dwbug.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPage {

    /**
     * 开始分页
     * @param list
     * @param pageNum 页码
     * @param pageSize 每页多少条数据
     * @return
     */
    public static Map<String,Object> startPage(List list, Integer pageNum,
                                 Integer pageSize) {
        if (list == null) {
            return null;
        }
        if (list.size() == 0) {
            return null;
        }
        if(pageNum==0){
            pageNum=1;

        }
        if(pageNum<0){
            return null;
        }
        Integer count = list.size(); // 记录总数
        Integer pageCount = 0; // 页数
        if (count % pageSize == 0) {
            pageCount = count / pageSize;
        } else {
            pageCount = count / pageSize + 1;
        }
        if(pageNum>pageCount){
            return null;
        }

        int fromIndex = 0; // 开始索引
        int toIndex = 0; // 结束索引

        if (pageNum != pageCount) {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = fromIndex + pageSize;
        } else {
            fromIndex = (pageNum - 1) * pageSize;
            toIndex = count;
        }

        List pageList = list.subList(fromIndex, toIndex);

        Map<String,Object> map = new HashMap<>();
        map.put("count",count);
        map.put("pageCount",pageCount);
        map.put("pageNum",pageNum);
        map.put("pageSize",pageSize);
        map.put("records",pageList);
//        List list1 = new ArrayList();
//        list1.add(map);
        return map;
    }
}
