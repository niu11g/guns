package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cinema/")
public class CinemaController {

    @Reference(interfaceClass = CinemaAPI.class,check=false)
    private CinemaAPI cinemaAPI;

    @RequestMapping(value="getCinemas")
    //查询影院列表
    public ResponseVO getCinemas(CinemaRequestVO cinemaVO){
        try {
            //按照五个条件进行筛选
            Page<CinemaVO> cinemas = cinemaAPI.getCinemas(cinemaVO);
            //判断是否有满足条件的影院
            if(cinemas.getRecords() == null || cinemas.getRecords().size() == 0 ){
                return ResponseVO.success("没有影院信息");
            }else{
                return ResponseVO.success(cinemas.getCurrent(),(int)cinemas.getPages(),"",cinemas.getRecords());
            }
        }catch (Exception e){
            //如果出现异常，应该如何处理
            log.error("获取影院列表异常",e);
            return ResponseVO.serviceFail("查询影院列表失败");
        }
    }
    //获取影院列表
    @RequestMapping(value="getCondition")
    public ResponseVO getCondition(CinemaRequestVO cinemaVO){
        try {
            List<BrandVO> brands = cinemaAPI.getBrands(cinemaVO.getBrandId());
            List<AreaVO> areas = cinemaAPI.getAreas(cinemaVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaAPI.getHallTypes(cinemaVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setAreaList(areas);
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);

            return ResponseVO.success(cinemaConditionResponseVO);
        }catch (Exception e){
            log.error("获取条件列表失败",e);
            return ResponseVO.serviceFail("获取影院查询条件失败");
        }
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
