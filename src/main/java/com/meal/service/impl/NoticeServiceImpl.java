package com.meal.service.impl;

import com.meal.entity.Notice;
import com.meal.repository.NoticeRepository;
import com.meal.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public Notice getById(String id) {
        return noticeRepository.findById(id).orElse(null);
    }

    @Override
    public List<Notice> getAll() {
        return noticeRepository.findAllByOrderByCreateTimeDesc();
    }

    @Override
    public List<Notice> getLatest(int limit) {
        return getAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Notice notice) {
        notice.setId(UUID.randomUUID().toString());
        noticeRepository.save(notice);
    }

    @Override
    public void update(Notice notice) {
        noticeRepository.save(notice);
    }

    @Override
    public void delete(String id) {
        noticeRepository.deleteById(id);
    }
}
