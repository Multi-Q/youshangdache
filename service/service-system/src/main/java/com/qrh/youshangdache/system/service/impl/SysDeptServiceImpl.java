package com.qrh.youshangdache.system.service.impl;

import com.qrh.youshangdache.common.execption.GuiguException;
import com.qrh.youshangdache.common.result.ResultCodeEnum;
import com.qrh.youshangdache.model.entity.system.SysDept;
import com.qrh.youshangdache.system.helper.DeptHelper;
import com.qrh.youshangdache.system.mapper.SysDeptMapper;
import com.qrh.youshangdache.system.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;


    @Override
    public List<SysDept> findNodes() {
        List<SysDept> goodsTypeList = this.list();
        return DeptHelper.buildTree(goodsTypeList, 0L);
    }

    @Override
    public List<SysDept> findUserNodes() {
        List<SysDept> sysDeptList = this.list(new LambdaQueryWrapper<SysDept>().eq(SysDept::getStatus, 1));
        return DeptHelper.buildTree(sysDeptList, 0L);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysDept sysDept = this.getById(id);
        sysDept.setStatus(status);
        this.updateById(sysDept);
    }



    @Override
    public boolean removeById(Serializable id) {
        long count = this.count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if(count > 0) {
            throw new GuiguException(ResultCodeEnum.NODE_ERROR);
        }
        sysDeptMapper.deleteById(id);
        return false;
    }

}

