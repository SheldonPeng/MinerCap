package org.qgstudio.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDao {

    @Select("select * from userList where user_id = #{userId}")
    void Test(int userId);
}
