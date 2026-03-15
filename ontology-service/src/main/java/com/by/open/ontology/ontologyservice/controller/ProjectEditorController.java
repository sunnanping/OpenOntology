package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.entity.Comment;
import com.by.open.ontology.ontologyservice.entity.ProjectActivity;
import com.by.open.ontology.ontologyservice.repository.CommentRepository;
import com.by.open.ontology.ontologyservice.repository.ProjectActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/ontology/project-editor")
public class ProjectEditorController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectActivityRepository projectActivityRepository;

    // ==================== 评论相关接口 ====================

    @GetMapping("/comments/{projectId}")
    public List<Comment> getCommentsByProject(@PathVariable String projectId) {
        return commentRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @GetMapping("/comments/{projectId}/entity/{entityId}")
    public List<Comment> getCommentsByEntity(@PathVariable String projectId, @PathVariable String entityId) {
        return commentRepository.findByProjectIdAndEntityId(projectId, entityId);
    }

    @PostMapping("/comments")
    public Comment createComment(@RequestBody Comment comment) {
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());
        Comment savedComment = commentRepository.save(comment);
        
        // 记录活动
        ProjectActivity activity = new ProjectActivity();
        activity.setProjectId(comment.getProjectId());
        activity.setAction("Added comment");
        activity.setEntityType("COMMENT");
        activity.setEntityId(savedComment.getId());
        activity.setUsername(comment.getAuthor());
        projectActivityRepository.save(activity);
        
        return savedComment;
    }

    @PutMapping("/comments/{id}")
    public Comment updateComment(@PathVariable String id, @RequestBody Comment comment) {
        Comment existingComment = commentRepository.findById(id).orElse(null);
        if (existingComment != null) {
            existingComment.setContent(comment.getContent());
            existingComment.setUpdatedAt(new Date());
            return commentRepository.save(existingComment);
        }
        return null;
    }

    @DeleteMapping("/comments/{id}")
    public void deleteComment(@PathVariable String id) {
        commentRepository.deleteById(id);
    }

    // ==================== 项目动态相关接口 ====================

    @GetMapping("/activities/{projectId}")
    public List<ProjectActivity> getProjectActivities(@PathVariable String projectId) {
        return projectActivityRepository.findTop20ByProjectIdOrderByTimestampDesc(projectId);
    }

    @PostMapping("/activities")
    public ProjectActivity createActivity(@RequestBody ProjectActivity activity) {
        activity.setTimestamp(new Date());
        return projectActivityRepository.save(activity);
    }

    // ==================== 类层次结构相关接口 ====================

    @GetMapping("/class-hierarchy/{projectId}")
    public List<ClassHierarchyNode> getClassHierarchy(@PathVariable String projectId) {
        // TODO: 从OWL文件或数据库中解析类层次结构
        // 这里返回模拟数据
        return List.of(
            new ClassHierarchyNode("1", "owl:Thing", null, List.of("2", "3")),
            new ClassHierarchyNode("2", "ICT领域", "1", List.of("4", "5")),
            new ClassHierarchyNode("3", "物理位置", "1", List.of("6")),
            new ClassHierarchyNode("4", "应用架构", "2", List.of()),
            new ClassHierarchyNode("5", "数据架构", "2", List.of()),
            new ClassHierarchyNode("6", "路由器", "3", List.of())
        );
    }

    // 内部类：类层次结构节点
    public static class ClassHierarchyNode {
        private String id;
        private String name;
        private String parentId;
        private List<String> childrenIds;

        public ClassHierarchyNode(String id, String name, String parentId, List<String> childrenIds) {
            this.id = id;
            this.name = name;
            this.parentId = parentId;
            this.childrenIds = childrenIds;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getParentId() { return parentId; }
        public void setParentId(String parentId) { this.parentId = parentId; }
        public List<String> getChildrenIds() { return childrenIds; }
        public void setChildrenIds(List<String> childrenIds) { this.childrenIds = childrenIds; }
    }
}
