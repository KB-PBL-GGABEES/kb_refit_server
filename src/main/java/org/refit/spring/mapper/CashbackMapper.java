package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CashbackMapper {
    @Select("SELECT * FROM reward")
    void get();

    @Select("")
    void getTotalPoint();

}
