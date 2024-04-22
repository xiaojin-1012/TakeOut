package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 插入购物车数据
     *
     * @param
     * @return
     */
    List<ShoppingCart> showShoppingCart();
    /**
     * 清除购物车
     *
     * @param
     * @return
     */
    void clean();

    /**
     * 删除购物车单个商品
     *
     * @param shoppingCartDTO
     * @return
     */
    void delete(ShoppingCartDTO shoppingCartDTO);
}
