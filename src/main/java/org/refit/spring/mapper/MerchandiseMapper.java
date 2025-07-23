package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.merchandise.entity.Merchandise;

@Mapper
public interface MerchandiseMapper {
    @Select("SELECT * FROM merchandise WHERE merchandise_id = #{merchandiseId}")
    Merchandise findByMerchandiseId(Long merchandiseId);
}
