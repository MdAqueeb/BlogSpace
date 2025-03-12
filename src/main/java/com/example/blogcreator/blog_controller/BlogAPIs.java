package com.example.blogcreator.blog_controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.blogcreator.blog_entities.BlogTable;
import com.example.blogcreator.blog_service.BlogLogic;
import com.example.blogcreator.blog_service.OpenAIService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class BlogAPIs {

    @Autowired
    private BlogLogic LogicBlog;

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("Add_blog")
    public ResponseEntity<BlogTable> Add_Blog(@RequestBody BlogTable blog) {

        try{
            BlogTable response = LogicBlog.AddBlog(blog);
            if(response == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST ,"The blog not added because value are incorrect");
            }
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occurred while adding the blog");
        }
        
    }
    
    @GetMapping("Blog_List/{pageNo}/{blogsLimit}")
    public ResponseEntity<List<BlogTable>> get_Blogs(@PathVariable int pageNo) {
        try{
            Page<BlogTable> allblogs = LogicBlog.BlogList(pageNo);
            List<BlogTable> content = allblogs.getContent();
            return ResponseEntity.ok(content);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"An unexpected error occured while adding the blog");
        }
    }
    
    @GetMapping("Get_Blog/{id}")
    public ResponseEntity<BlogTable> get_Blog(@PathVariable long id) {
        try{
            Optional<BlogTable> getblog = LogicBlog.GetBlog(id);
            if(!getblog.isPresent()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The blog not found");
            }
            return ResponseEntity.ok(getblog.get());
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e+"\nAn error Occur ");
        }
        
    }

    @PutMapping("Update_Blog/{id}")
    public ResponseEntity<BlogTable> update_Blog(@PathVariable long id, @RequestBody BlogTable newBlog) {
        try{
            BlogTable update = LogicBlog.ModifyBlog(id, newBlog);
            if(update == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The blog is not found");
            }
            return new ResponseEntity<>(update,HttpStatus.OK);
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e+"\nAn error Occur");
        }
    }

    @DeleteMapping("Remove_Blog/{id}")
    public ResponseEntity<BlogTable> delete_Blog(@PathVariable long id){
        System.out.println(id);
        try{
            Optional<BlogTable> remove = LogicBlog.RemoveBlog(id);
            if(remove.isPresent()){
                return new ResponseEntity<>(remove.get(),HttpStatus.OK);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"The blog not found");
        }
        catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e+"\nAn error occur");
        }
    }

    @GetMapping("CSRF")
    public CsrfToken getCSRF(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @PostMapping("/summarize")
    public String summarizeBlog(@RequestBody String blogContent) {
        return openAIService.generateSummary(blogContent);
    }


        
    
}
