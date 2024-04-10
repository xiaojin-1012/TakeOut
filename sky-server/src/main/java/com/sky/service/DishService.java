package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 新增菜品和对应的口味数据
     *
     * @param dishDTO
     * @return
     */

    public void saveWithFlavor(DishDTO dishDTO);
    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 菜品批量删除
     *
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
    /**
     * 根据id查询菜品
     *
     * @param id
     * @return
     */
    DishVO getByIdwithFlavor(Long id);
    /**
     * 修改菜品信息
     *
     * @param dishDTO
     * @return
     */
    void updatewithDlavor(DishDTO dishDTO);
    /**
     * 菜品起售停售
     *
     * @param status
     * @return
     */
    void satrtorStop(Integer status, Long id);
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);
}
