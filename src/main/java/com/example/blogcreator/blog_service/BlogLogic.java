package com.example.blogcreator.blog_service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.blogcreator.blog_entities.BlogTable;
import com.example.blogcreator.blog_repository.BlogQueries;

import jakarta.transaction.Transactional;

@Service
public class BlogLogic {
    
    @Autowired
    private BlogQueries blogOperations;

    public BlogTable AddBlog(BlogTable blog){
        return blogOperations.save(blog);
    }

    public Page<BlogTable> BlogList(int pageNo){
        
        PageRequest page = PageRequest.of(pageNo, 7);
        return blogOperations.findAll(page);
    }

    public Optional<BlogTable> GetBlog(long id){
        return blogOperations.findById(id);
    }

    public BlogTable ModifyBlog(long id,BlogTable blogUpdate){
        Optional<BlogTable> blog = blogOperations.findById(id);
        if(blog.isPresent()){
            blog.get().setTitle(blogUpdate.getTitle());
            blog.get().setContent(blogUpdate.getContent());
            blog.get().setAuthor(blogUpdate.getAuthor());
            return blogOperations.save(blog.get());
        }
        return null;
    }



    @Transactional
    public Optional<BlogTable> RemoveBlog(long id){

        Optional<BlogTable> blog = blogOperations.findById(id);
        System.out.println(blog.get().getAuthor());
        if(blog.isPresent()){
            int rowDeleted = blogOperations.removeBlog(id);
            if(rowDeleted > 0){
                return blog;
            }
        }

        return Optional.empty();
        
    }
}
