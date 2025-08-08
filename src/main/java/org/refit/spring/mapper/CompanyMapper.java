package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.pos.entity.Company;

import java.util.List;

@Mapper
public interface CompanyMapper {
    @Select("SELECT * FROM company WHERE ceo_id = #{userId}")
    List<Company> findAllByUserId(@Param("userId") Long userId);
}
