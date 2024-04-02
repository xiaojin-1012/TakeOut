package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 新增菜品和对应的口味数据
     *
     * @param flavors
     * @return
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     *
     * @param
     * @return
     */
    @Delete("delete from sky_take_out.dish_flavor where id=#{id}")
    void deleteByDishId(Long id);

    /**
     * 根据id查询菜品
     *
     * @param dishId
     * @return
     */
    @Select("select *from sky_take_out.dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
