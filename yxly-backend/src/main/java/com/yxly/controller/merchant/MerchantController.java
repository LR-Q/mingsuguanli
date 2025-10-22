package com.yxly.controller.merchant;

import com.yxly.common.Result;
import com.yxly.dto.request.MerchantRegisterRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商户管理控制器
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/v1/merchant")
@RequiredArgsConstructor
@Validated
@Tag(name = "商户管理", description = "民宿主商户注册、管理相关接口")
public class MerchantController {
    
    private final MerchantService merchantService;
    
    @Operation(summary = "民宿主管理员注册", description = "民宿主注册账号，需要平台审核通过后才能使用")
    @PostMapping("/register")
    public Result<LoginResponse> registerMerchant(@Valid @RequestBody MerchantRegisterRequest request) {
        log.info("民宿主管理员注册请求: username={}, realName={}", 
                 request.getUsername(), request.getRealName());
        
        LoginResponse response = merchantService.registerMerchant(request);
        
        return Result.success(response, "注册成功！您的账号正在审核中，审核通过后即可登录使用");
    }
    
}
