package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDishes(SetmealDTO setmealDTO);

    void deleteByIds(List<Long> ids);

    SetmealVO getById(Long id);

    void updateWithDishes(SetmealDTO setmealDTO);
}
