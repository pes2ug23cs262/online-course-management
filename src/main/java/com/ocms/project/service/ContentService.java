package com.ocms.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocms.project.model.Content;
import com.ocms.project.repository.ContentRepository;

@Service
public class ContentService {

    @Autowired
    private ContentRepository contentRepository;

    public Content addCourseContent(Long courseId, String title, String type, String url) {
        Content content = new Content(courseId, title, type, url);
        return contentRepository.save(content);
    }

    public List<Content> getContentForCourse(Long courseId) {
        return contentRepository.findByCourseId(courseId);
    }
}
