package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaAPI.class,loadbalance = "roundrobin",retries = -1)
public class CinemaServiceImpl implements CinemaAPI {

    @Autowired
    private MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    private MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    private MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    private MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    private MoocHallFilmInfoTMapper moocHallFilmInfoTMapper;
    @Autowired
    private MoocFieldTMapper moocFieldTMapper;

    @Override
    public Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO) {
        //判断是否传入查询条件 -> brandId,distId,hallType 是否--99
        List<CinemaVO> cinemas = new ArrayList<>();

        Page<MoocCinemaT> page = new Page<>(cinemaRequestVO.getNowPage(),cinemaRequestVO.getPageSize());
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if(cinemaRequestVO.getBrandId() !=99){
            entityWrapper.eq("brand_id",cinemaRequestVO.getBrandId());
        }
        if(cinemaRequestVO.getDistrictId() != 99){
            entityWrapper.eq("area_id",cinemaRequestVO.getDistrictId());
        }
        if(cinemaRequestVO.getHallType() != 99){
            entityWrapper.like("hall_ids","%#+"+cinemaRequestVO.getHallType());
        }
        //将数据实体转换为业务实体
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page,entityWrapper);
        for(MoocCinemaT moocCinemaT : moocCinemaTS){
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(moocCinemaT.getUuid());
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice());
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());
            cinemas.add(cinemaVO);
        }
        //根据条件，判断影院列表总数
        long counts = moocCinemaTMapper.selectCount(entityWrapper);

        Page<CinemaVO> result = new Page();
        result.setRecords(cinemas);
        result.setSize(cinemaRequestVO.getPageSize());
        result.setTotal(counts);
        return result;
    }

    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag = false;
        List<BrandVO> brands = new ArrayList<>();
        //判断brandId是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        //判断brandId 是否等于99
        if(brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null){
            flag = true;
        }
        //查询所有列表
        List<MoocBrandDictT> moocBrandDictTS = moocBrandDictTMapper.selectList(null);
        //判断flag如果为true,则将99置为isActive
        for(MoocBrandDictT brand : moocBrandDictTS){
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandName(brand.getShowName());
            brandVO.setBrandId(brand.getUuid());
            if(flag){
                if(brand.getUuid() == 99){
                    brandVO.setActive(true);
                }
            }else{
                if(brand.getUuid() == brandId){
                    brandVO.setActive(true);
                }
            }
            brands.add(brandVO);

        }
        return brands;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areas = new ArrayList<>();
        //判断brandId是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        //判断brandId 是否等于99
        if(areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null){
            flag = true;
        }
        //查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);
        //判断flag如果为true,则将99置为isActive
        for(MoocAreaDictT area : moocAreaDictTS){
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaName(area.getShowName());
            areaVO.setAreaId(area.getUuid());
            if(flag){
                if(area.getUuid() == 99){
                    areaVO.setActive(true);
                }
            }else{
                if(area.getUuid() == areaId){
                    areaVO.setActive(true);
                }
            }
            areas.add(areaVO);
        }
        return areas;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> halls = new ArrayList<>();
        //判断brandId是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        //判断brandId 是否等于99
        if(hallType == 99 || moocHallDictT == null || moocHallDictT.getUuid() == null){
            flag = true;
        }
        //查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);
        //判断flag如果为true,则将99置为isActive
        for(MoocHallDictT hall : moocHallDictTS){
            HallTypeVO hallVO = new HallTypeVO();
            hallVO.setHalltypeName(hall.getShowName());
            hallVO.setHalltypeId(hall.getUuid());
            if(flag){
                if(hall.getUuid() == 99){
                    hallVO.setActive(true);
                }
            }else{
                if(hall.getUuid() == hallType){
                    hallVO.setActive(true);
                }
            }
            halls.add(hallVO);
        }
        return halls;
    }

    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        //数据实体
        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);
        //将数据实体转换成业务实体
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setCinemaAddress(moocCinemaT.getCinemaAddress());
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid()+"");

        return cinemaInfoVO;
    }

    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {
        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);
        return hallInfoVO;
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);
        return filmInfoVO;
    }
}
