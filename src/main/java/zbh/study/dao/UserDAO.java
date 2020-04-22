package zbh.study.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zbh.study.domain.User;


@Mapper
public interface UserDAO {
    @Select("select * from user where id=#{id}")
    public User findUserById(@Param("id") Long id);

    @Insert("insert into user(id,nickname,password,salt,login_count,register_date,last_login_date) values(#{u.id},#{u.nickname},#{u.password},#{u.salt},#{u.loginCount},#{u.registerDate},#{u.lastLoginDate})")
    public int addUser(@Param("u") User user);

}
