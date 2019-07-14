package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaAPI.class)
    private CinemaAPI cinemaAPI;

    @RequestMapping(value="getCinemas",method = RequestMethod.GET)
    //查询影院列表
    public ResponseVO getCinemas(CinemaVO cinemaVO){

        return null;
    }
    //获取影院列表
    @RequestMapping(value="getCondition",method=RequestMethod.GET)
    public ResponseVO getCondition(CinemaVO cinemaVO){
        return null;
    }
    //获取播放场次
    @RequestMapping(value="getFields")
    public ResponseVO getFields(int cinemaId){
        return null;
    }
    //获取场次详细信息
    @RequestMapping(value="getFieldInfo",method=RequestMethod.POST)
    public ResponseVO getFieldInfo(int cinemaId,int fieldId){
        return null;
    }



}
