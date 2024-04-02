package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> flavors);
    /**
     * 根据菜品id删除对应的口味数据
     *
     * @param
     * @return
     */
    @Delete("delete from sky_take_out.dish_flavor where id=#{id}")
    void deleteByDishId(Long id);
}
