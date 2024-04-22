package com.sky.service;

import com.sky.entity.Dish;
import com.sky.vo.DishVO;

import java.util.List;

public interface UserDishService {
    /**
     * 根据分类id查询菜品
     *
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
