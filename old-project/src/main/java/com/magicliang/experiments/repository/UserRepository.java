package com.magicliang.experiments.repository;


import com.magicliang.experiments.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by magicliang on 2016/4/23.
 */
//Can also use JpaRepository here
//Don't even need one implementation
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);

    //Use SpEL here.
    @Query("select u from #{#entityName} u where u.name = :name")
    List<User> findByName1(@Param("name") String name);

}
