package com.sky.controller.admin;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "管理端营业状态接口")
public class ShopController {

    private final static String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 修改营业状态
     * @param status
     * @return
     */
    @ApiOperation("修改营业状态")
    @PutMapping("/{status}")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("修改营业状态为{}", (status==1?"营业":"打烊"));
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @ApiOperation("获取营业状态")
    @GetMapping("/status")
    public Result getShopStatus() {
//        String status = (String) redisTemplate.opsForValue().get(KEY);
//        Integer statusInt =Integer.parseInt(status);
        Integer statusInt = (Integer)redisTemplate.opsForValue().get(KEY);
        return Result.success(statusInt);
    }
}
