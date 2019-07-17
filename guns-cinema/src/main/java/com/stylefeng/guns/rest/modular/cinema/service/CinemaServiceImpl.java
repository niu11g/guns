package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.CinemaAPI;
import com.stylefeng.guns.api.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Service(interfaceClass = CinemaAPI.class)
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
        return null;
    }

    @Override
    public List<BrandVO> getBrands(int brandId) {
        return null;
    }

    @Override
    public List<AreaVO> getAreas(int areaId) {
        return null;
    }

    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        return null;
    }

    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        return null;
    }

    @Override
    public FilmInfoVO getFilmInfoByCinemaId(int cinemaId) {
        return null;
    }

    @Override
    public FilmFieldVO getFilmFieldInfo(int fileldId) {
        return null;
    }

    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {
        return null;
    }
}
