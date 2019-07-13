package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.api.user.UserInfoModel;
import com.stylefeng.guns.api.user.UserModel;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.MoocUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocUserT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Service(interfaceClass = UserAPI.class,loadbalance = "roundrobin")
public class UserImpl implements UserAPI {

    @Autowired
    private MoocUserTMapper moocUserTMapper;

    @Override
    public boolean register(UserModel userModel) {
        //将注册信息实体转换为数据实体【mooc_user_t】
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(userModel.getUsername());
        moocUserT.setAddress(userModel.getAddress());
        moocUserT.setUserPhone(userModel.getPhone());
        moocUserT.setEmail(userModel.getEmail());
        //数据加密 【MD5混淆加密+盐值->Shiro 加密】
        String md5Password = MD5Util.encrypt(userModel.getPassword());
        moocUserT.setUserPwd(md5Password);
        //将数据实体存入数据库
        int result = moocUserTMapper.insert(moocUserT);
        if(result > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public int getUser(String username, String password) {
        //根据登录账号获取数据库信息
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserName(username);

        MoocUserT result = moocUserTMapper.selectOne(moocUserT);

        //根据获取到的结果,然后与加密以后的密码做匹配
        if(result!=null && result.getUuid()>0){
            String md5Password = MD5Util.encrypt(password);
            if(result.getUserPwd().equals(md5Password)){
                return result.getUuid();
            }
        }
        return 0;
    }



    @Override
    public boolean checkUsername(String username) {
        EntityWrapper<MoocUserT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("user_name",username);
        Integer result = moocUserTMapper.selectCount(entityWrapper);
        if(result!=null && result>0){
            return false;
        }else{
            return true;
        }
    }

    private UserInfoModel do2UserInfo(MoocUserT moocUserT){
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setUsername(moocUserT.getUserName());
        userInfoModel.setUpdateTime(moocUserT.getUpdateTime().getTime());
        userInfoModel.setSex(moocUserT.getUserSex());
        userInfoModel.setPhone(moocUserT.getUserPhone());
        userInfoModel.setNickname(moocUserT.getNickName());
        userInfoModel.setLifeState(""+moocUserT.getLifeState());
        userInfoModel.setHeadAddress(moocUserT.getHeadUrl());
        userInfoModel.setEmail(moocUserT.getEmail());
        userInfoModel.setCreateTime(moocUserT.getBeginTime().getTime());
        userInfoModel.setBirthday(moocUserT.getBirthday());
        userInfoModel.setBiography(moocUserT.getBiography());
        userInfoModel.setAddress(moocUserT.getAddress());
        return userInfoModel;
    }

    @Override
    public UserInfoModel getUserInfo(int uuid) {
        //根据主键查询用户信息 MoocUserT
        MoocUserT moocUserT = moocUserTMapper.selectById(uuid);
        //把MoocUserT类型转换为UserInfoModel
        UserInfoModel userInfoModel =do2UserInfo(moocUserT);
        //返回UserInfoModel
        return userInfoModel;
    }

    private Date time2Date(long time){
        Date date = new Date(time);
        return date;
    }

    @Override
    public UserInfoModel updateUserInfo(UserInfoModel userInfoModel) {
        //将传入的数据转换为MoocUserT
        MoocUserT moocUserT = new MoocUserT();
        moocUserT.setUserSex(userInfoModel.getSex());
        moocUserT.setUpdateTime(time2Date(System.currentTimeMillis()));
        moocUserT.setNickName(userInfoModel.getNickname());
        moocUserT.setLifeState(Integer.parseInt(userInfoModel.getLifeState()));
        moocUserT.setHeadUrl(userInfoModel.getHeadAddress());
        moocUserT.setBirthday(userInfoModel.getBirthday());
        moocUserT.setBiography(userInfoModel.getBiography());
//        moocUserT.setBeginTime(time2Date(userInfoModel.getCreateTime()));
        moocUserT.setAddress(userInfoModel.getAddress());
        moocUserT.setUserPhone(userInfoModel.getPhone());
        moocUserT.setEmail(userInfoModel.getEmail());
        moocUserT.setUuid(userInfoModel.getUuid());
       //将数据存入数据库
        Integer isSuccess = moocUserTMapper.updateById(moocUserT);
        //判断修改是否成功
        if(isSuccess>0){
            //根据id获取用户信息
            UserInfoModel userInfo = getUserInfo(moocUserT.getUuid());
            //返回给前端
            return userInfo;
        }else{
            //修改之前的用户返回给前端
            return userInfoModel;
        }
    }
}
