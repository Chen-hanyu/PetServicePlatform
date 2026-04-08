package com.petplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petplatform.entity.MerchantService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface MerchantServiceMapper extends BaseMapper<MerchantService> {

    @Select("SELECT id FROM merchant_services WHERE id = #{serviceId} FOR UPDATE")
    Long lockById(@Param("serviceId") Long serviceId);
}
