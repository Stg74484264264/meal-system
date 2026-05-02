package com.meal.service;

import com.meal.entity.Notice;

import java.util.List;

/**
 * 公告服务接口
 */
public interface NoticeService {

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告对象
     */
    Notice getById(String id);

    /**
     * 查询所有公告
     *
     * @return 公告列表
     */
    List<Notice> getAll();

    /**
     * 获取最新公告
     *
     * @param limit 数量限制
     * @return 公告列表
     */
    List<Notice> getLatest(int limit);

    /**
     * 新增公告
     *
     * @param notice 公告对象
     */
    void add(Notice notice);

    /**
     * 更新公告
     *
     * @param notice 公告对象
     */
    void update(Notice notice);

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    void delete(String id);
}