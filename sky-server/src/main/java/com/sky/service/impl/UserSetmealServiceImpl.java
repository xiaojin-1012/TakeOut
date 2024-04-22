package com.sky.service.impl;

import com.sky.entity.Setmeal;
import com.sky.mapper.UserSetmealMapper;
import com.sky.service.UserSetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class UserSetmealServiceImpl implements UserSetmealService {
    @Autowired
    private UserSetmealMapper userSetmealMapper;

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = userSetmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return userSetmealMapper.getDishItemBySetmealId(id);
    }
}
