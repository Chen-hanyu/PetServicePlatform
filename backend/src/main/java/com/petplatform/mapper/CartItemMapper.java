package com.petplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petplatform.entity.CartItem;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CartItemMapper extends BaseMapper<CartItem> {

    @Select("SELECT id FROM cart_items WHERE id = #{id} FOR UPDATE")
    Long lockById(@Param("id") Long id);
}
