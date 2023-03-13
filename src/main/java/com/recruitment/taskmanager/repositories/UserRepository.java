package com.recruitment.taskmanager.repositories;

import com.recruitment.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

}
