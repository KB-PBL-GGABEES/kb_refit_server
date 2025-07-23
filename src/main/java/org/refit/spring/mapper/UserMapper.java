package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.refit.spring.auth.entity.User;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    //refresh token 조회
    @Select("SELECT * FROM user WHERE refresh_token = #{refreshToken}")
    User findByRefreshToken(String refreshToken);

    //로그인시 refresh token 저장용
    @Update("UPDATE user SET refresh_token = #{refreshToken} WHERE username = #{username}")
    void updateRefreshToken(@Param("username") String username,
                            @Param("refreshToken") String refreshToken);

}
