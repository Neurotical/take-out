package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 添加口味
     * @param dishFlavors
     */
    void insert(List<DishFlavor> dishFlavors);

    /**
     * 根据菜品id删除口味
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);
}
