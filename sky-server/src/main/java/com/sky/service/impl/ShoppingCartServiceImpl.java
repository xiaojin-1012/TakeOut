package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //通过拦截器解析token中的方法BaseContext.getCurrentId()获取用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //判断当前加入到购物车中的商品是否已经存在
        if (list != null && list.size() > 0) {
            //如果已经存在，只需要将数量加一
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //如果不存在，需要插入购物车数据
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setName(dish.getName());
            } else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setName(setmeal.getName());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }
    /**
     * 插入购物车数据
     *
     * @param
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }
    /**
     * 清除购物车
     *
     * @param
     * @return
     */
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }


    /**
     * 删除购物车单个商品
     *
     * @param shoppingCartDTO
     * @return
     */
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if (list!=null&& list.size()>0){
            ShoppingCart shoppingCart1 = list.get(0);
            Integer number = shoppingCart1.getNumber();
            if (number==1){
                shoppingCartMapper.clean(shoppingCart1.getUserId());
            }else {
                shoppingCart1.setNumber(shoppingCart1.getNumber()-1);
                shoppingCartMapper.updateNumberById(shoppingCart1);
            };
        };
    }
}

