package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询是否存在对应的套餐
     * @param ids
     * @return
     */
    Integer countByDishesId(List<Long> ids);
}
