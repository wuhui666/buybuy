package zbh.study.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zbh.study.domain.User;


@Mapper
public interface UserDAO {
    @Select("select * from user where id=#{id}")
    public User findUserById(@Param("id") Long id);


}
