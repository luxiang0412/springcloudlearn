package com.luxiang.swaggerdemo.dao;

import com.luxiang.swaggerdemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
}
