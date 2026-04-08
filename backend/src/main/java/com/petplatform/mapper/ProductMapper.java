package com.petplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petplatform.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {

    @Update("""
            UPDATE products
            SET stock = stock - #{quantity},
                updated_at = NOW()
            WHERE id = #{productId}
              AND status = 'ON_SALE'
              AND stock >= #{quantity}
            """)
    int decrementStockSafely(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
