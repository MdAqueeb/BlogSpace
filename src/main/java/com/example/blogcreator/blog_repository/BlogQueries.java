package com.example.blogcreator.blog_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.blogcreator.blog_entities.BlogTable;

@Repository
public interface BlogQueries extends JpaRepository<BlogTable,Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM blog WHERE id =:id",nativeQuery = true)
    int removeBlog(@Param("id") long id);
    
}
