package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.merchandise.entity.Merchandise;

import java.util.List;

@Mapper
public interface MerchandiseMapper {
    @Select("SELECT * FROM merchandise WHERE merchandise_id = #{merchandiseId}")
    Merchandise findByMerchandiseId(Long merchandiseId);

    @Select("SELECT * FROM merchandise WHERE company_id = #{companyId}")
    List<Merchandise> findAllByCompanyId(Long companyId);

}
