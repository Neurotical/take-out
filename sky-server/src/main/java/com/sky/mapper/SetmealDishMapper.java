package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询是否存在对应的套餐
     * @param ids
     * @return
     */
    Integer countByDishesId(List<Long> ids);


    void save(List<SetmealDish> setmealDishes);
}
