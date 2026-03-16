package com.by.open.ontology.ontologyservice.controller;

import com.by.open.ontology.ontologyservice.dto.ClassHierarchyNode;
import com.by.open.ontology.ontologyservice.dto.CommentDto;
import com.by.open.ontology.ontologyservice.dto.ProjectActivityDto;
import com.by.open.ontology.ontologyservice.entity.Ontology;
import com.by.open.ontology.ontologyservice.service.OntologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private OntologyService ontologyService;

    @GetMapping("/hierarchy/{projectId}")
    public List<ClassHierarchyNode> getClassHierarchy(@PathVariable String projectId) {
        // 模拟类层级数据
        List<ClassHierarchyNode> hierarchy = new ArrayList<>();
        
        ClassHierarchyNode root = new ClassHierarchyNode();
        root.setId("owl:Thing");
        root.setName("owl:Thing");
        root.setIri("http://www.w3.org/2002/07/owl#Thing");
        root.setExpanded(true);
        
        List<ClassHierarchyNode> children = new ArrayList<>();
        
        // ICT领域
        ClassHierarchyNode ict = new ClassHierarchyNode("1", "ICT领域", "http://example.org/ICT", "ICT领域");
        ict.setChildren(Arrays.asList(
            new ClassHierarchyNode("1-1", "B域", "http://example.org/B", ""),
            new ClassHierarchyNode("1-2", "M域", "http://example.org/M", ""),
            new ClassHierarchyNode("1-3", "O域", "http://example.org/O", "")
        ));
        children.add(ict);
        
        // 应用架构
        ClassHierarchyNode appArch = new ClassHierarchyNode("2", "应用架构", "http://example.org/AppArch", "应用架构");
        appArch.setChildren(Arrays.asList(
            new ClassHierarchyNode("2-1", "一般系统", "http://example.org/General", ""),
            new ClassHierarchyNode("2-2", "核心系统", "http://example.org/Core", ""),
            new ClassHierarchyNode("2-3", "重要系统", "http://example.org/Important", "")
        ));
        children.add(appArch);
        
        // 数据架构
        children.add(new ClassHierarchyNode("3", "数据架构", "http://example.org/DataArch", "数据架构"));
        
        // 物理位置
        ClassHierarchyNode physical = new ClassHierarchyNode("4", "物理位置", "http://example.org/PhysicalLoc", "物理位置");
        ClassHierarchyNode office = new ClassHierarchyNode("4-1", "办公室", "http://example.org/Office", "");
        office.setChildren(Collections.singletonList(
            new ClassHierarchyNode("4-1-1", "物理位置", "http://example.org/Physical", "")
        ));
        physical.setChildren(Arrays.asList(
            office,
            new ClassHierarchyNode("4-2", "机房", "http://example.org/Room", ""),
            new ClassHierarchyNode("4-3", "机柜", "http://example.org/Cabinet", "")
        ));
        children.add(physical);
        
        // 路由器
        ClassHierarchyNode router = new ClassHierarchyNode("5", "路由器", "http://example.org/Router", "路由器");
        router.setChildren(Collections.singletonList(
            new ClassHierarchyNode("5-1", "cisco-G口路由器", "http://example.org/CiscoRouter", "")
        ));
        children.add(router);
        
        root.setChildren(children);
        hierarchy.add(root);
        
        return hierarchy;
    }

    @GetMapping("/{classId}")
    public ClassHierarchyNode getClassById(@PathVariable String classId) {
        // 模拟根据ID获取类信息
        return new ClassHierarchyNode(classId, "Class " + classId, "http://example.org/" + classId, "");
    }

    @PostMapping("/create")
    public ClassHierarchyNode createClass(@RequestBody ClassHierarchyNode classNode) {
        classNode.setId(UUID.randomUUID().toString());
        return classNode;
    }

    @PutMapping("/update/{classId}")
    public ClassHierarchyNode updateClass(@PathVariable String classId, @RequestBody ClassHierarchyNode classNode) {
        classNode.setId(classId);
        return classNode;
    }

    @DeleteMapping("/delete/{classId}")
    public void deleteClass(@PathVariable String classId) {
        // 删除类
    }

    @PostMapping("/move")
    public void moveClass(@RequestParam String classId, @RequestParam String newParentId) {
        // 移动类到新的父节点
    }

    @GetMapping("/comments/{entityId}")
    public List<CommentDto> getComments(@PathVariable String entityId) {
        List<CommentDto> comments = new ArrayList<>();
        
        CommentDto comment1 = new CommentDto();
        comment1.setId("1");
        comment1.setEntityId(entityId);
        comment1.setEntityType("Class");
        comment1.setAuthor("user1");
        comment1.setContent("This class needs more properties.");
        comment1.setCreatedAt(new Date(System.currentTimeMillis() - 3600000));
        comments.add(comment1);
        
        CommentDto comment2 = new CommentDto();
        comment2.setId("2");
        comment2.setEntityId(entityId);
        comment2.setEntityType("Class");
        comment2.setAuthor("user2");
        comment2.setContent("Agreed, we should add documentation.");
        comment2.setCreatedAt(new Date(System.currentTimeMillis() - 1800000));
        comments.add(comment2);
        
        return comments;
    }

    @PostMapping("/comments")
    public CommentDto addComment(@RequestBody CommentDto comment) {
        comment.setId(UUID.randomUUID().toString());
        comment.setCreatedAt(new Date());
        return comment;
    }

    @GetMapping("/activities/{projectId}")
    public List<ProjectActivityDto> getProjectActivities(@PathVariable String projectId) {
        List<ProjectActivityDto> activities = new ArrayList<>();
        
        ProjectActivityDto activity1 = new ProjectActivityDto();
        activity1.setId("1");
        activity1.setProjectId(projectId);
        activity1.setAction("Created class \"ICT领域\"");
        activity1.setAuthor("bjsun07");
        activity1.setEntityType("Class");
        activity1.setEntityName("ICT领域");
        activity1.setTimestamp(new Date(System.currentTimeMillis() - 86400000));
        activities.add(activity1);
        
        ProjectActivityDto activity2 = new ProjectActivityDto();
        activity2.setId("2");
        activity2.setProjectId(projectId);
        activity2.setAction("Modified property \"hasName\"");
        activity2.setAuthor("bjsun07");
        activity2.setEntityType("Property");
        activity2.setEntityName("hasName");
        activity2.setTimestamp(new Date(System.currentTimeMillis() - 43200000));
        activities.add(activity2);
        
        ProjectActivityDto activity3 = new ProjectActivityDto();
        activity3.setId("3");
        activity3.setProjectId(projectId);
        activity3.setAction("Added individual \"Router01\"");
        activity3.setAuthor("bjsun07");
        activity3.setEntityType("Individual");
        activity3.setEntityName("Router01");
        activity3.setTimestamp(new Date(System.currentTimeMillis() - 3600000));
        activities.add(activity3);
        
        return activities;
    }

    @GetMapping("/search")
    public List<ClassHierarchyNode> searchClasses(@RequestParam String query, @RequestParam String projectId) {
        List<ClassHierarchyNode> results = new ArrayList<>();
        // 模拟搜索结果
        if (query.toLowerCase().contains("ict")) {
            results.add(new ClassHierarchyNode("1", "ICT领域", "http://example.org/ICT", "ICT领域"));
        }
        if (query.toLowerCase().contains("router")) {
            results.add(new ClassHierarchyNode("5", "路由器", "http://example.org/Router", "路由器"));
        }
        return results;
    }
}
