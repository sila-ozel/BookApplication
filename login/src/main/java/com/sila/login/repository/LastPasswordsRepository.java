package com.sila.login.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import javax.transaction.Transactional;

import com.sila.login.model.LastPasswords;

@Repository
public interface LastPasswordsRepository extends JpaRepository<LastPasswords,Long>{
    //this query should return exactly one row
    @Query(value = "select lp from LastPasswords lp where lp.user.username = :username and lp.pw_no = :pw_no")
    LastPasswords findPwByNameAndNo(@Param("username") String username, @Param("pw_no") int pw_no);

    @Query(value = "select lp from LastPasswords lp where lp.user.username = :username order by lp.pw_no asc")
    List<LastPasswords> findPwsByUsername(@Param("username") String username);
   
    @Transactional
    @Modifying
    @Query(value = "update LastPasswords lp set lp.pw = :pw where lp.user.username = :username and lp.pw_no = :pw_no")
    int updatePw(@Param("username") String username, @Param("pw_no") int pw_no,  @Param("pw") String pw);

}
