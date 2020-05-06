package zbh.study.dao;

import org.apache.ibatis.annotations.*;
import zbh.study.domain.User;


@Mapper
public interface UserDAO {
    @Select("select * from user where id=#{id}")
    public User findUserById(@Param("id") Long id);

    @Insert("insert into user(id,nickname,password,salt,address,register_date) values(#{u.id},#{u.nickname},#{u.password},#{u.salt},#{u.address},#{u.registerDate})")
    public int addUser(@Param("u") User user);

    @Update("update user set nickname=#{u.nickname},password=#{u.password},address=#{u.address} where id=#{u.id}")
    public int updateById(@Param("u") User user);
}
