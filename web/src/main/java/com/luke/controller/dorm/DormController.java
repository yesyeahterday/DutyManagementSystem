package com.luke.controller.dorm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luke.entity.Dorm;
import com.luke.result.Result;
import com.luke.service.DormService;
import com.luke.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    管理宿舍信息，增删查改楼栋
 */
@RestController
@Tag(name = "管理楼栋信息")
@RequestMapping("/dorm")
public class DormController {

    @Autowired
    private DormService dormService;
    @Autowired
    private ZoneService zoneService;

    @PostMapping("/create")
    @Operation(summary = "批量新增楼栋信息")
    @PreAuthorize("hasRole('LEADER')")
    public Result createBuilding(@RequestBody List<Dorm> dorms) {
        // 校验每个楼栋的zone是否存在
        for (Dorm dorm : dorms) {
            // 检查片区是否存在
            System.out.println(dorm);
            if (!zoneService.checkZoneExists(dorm.getZone())) {
                return Result.fail("片区[" + dorm.getZone() + "]不存在，请先创建片区");
            }
            // 检查楼栋名称唯一性
            if (dormService.checkDormNameExists(dorm.getName())) {
                return Result.fail("楼栋名称[" + dorm.getName() + "]已存在");
            }
        }
        // 批量保存
        boolean success = dormService.saveBatch(dorms);
        return success ? Result.ok("创建成功") : Result.fail("创建失败");
    }

    // 2. 更新基础信息,根据宿舍名字修改信息
    @Operation(summary = "更新给定楼栋的信息")
    @PreAuthorize("hasRole('LEADER')")
    @PostMapping("/update")
    public Result updateBuilding(
            @RequestBody Dorm dorm) {
        // 参数校验
        if (dorm.getId() == null) {
            return Result.fail("楼栋ID不能为空");
        }

        // 获取现有数据
        Dorm existingDorm = dormService.getById(dorm.getId());
        if (existingDorm == null) {
            return Result.fail("楼栋ID不存在");
        }

        // 动态更新校验
        if (dorm.getName() != null && !dorm.getName().equals(existingDorm.getName())) {
            if (dormService.checkDormNameExists(dorm.getName())) {
                return Result.fail("修改后的楼栋名称[" + dorm.getName() + "]已存在");
            }
        }

        if (dorm.getZone() != null && !dorm.getZone().equals(existingDorm.getZone())) {
            if (!zoneService.checkZoneExists(dorm.getZone())) {
                return Result.fail("修改的片区[" + dorm.getZone() + "]不存在");
            }
        }

        // 执行动态字段更新
        boolean success = dormService.updatePartial(dorm);
        return success ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    // 3. 按区域分页查询楼栋信息
    @Operation(summary = "按区域分页查询楼栋信息")
    @GetMapping("/get")
    @PreAuthorize("hasAnyRole('ZONEKEEPER','LEADER')")
    public Result listBuildings(
            @RequestParam("zone") String zone,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 构建分页对象
        Page<Dorm> pageInfo = new Page<>(page, size);
        // 构建查询条件
        LambdaQueryWrapper<Dorm> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dorm::getZone, zone)
                .eq(Dorm::getIsDeleted, 0);
        // 执行分页查询
        dormService.page(pageInfo, queryWrapper);
        return Result.ok(pageInfo);
    }
}
