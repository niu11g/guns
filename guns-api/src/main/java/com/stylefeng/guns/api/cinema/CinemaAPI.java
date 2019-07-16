package com.stylefeng.guns.api.cinema;


import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.cinema.vo.*;

import java.util.List;

public interface CinemaAPI {
    //根据CinemaQueryVO,查询影院列表
    Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO);
    //根据条件获取品牌列表
    List<BrandVO> getBrands(int brandId);
    //获取行政区域列表
    List<AreaVO> getAreas(int areaId);
    //获取影厅类型列表
    List<HallTypeVO> getHallTypes(int hallType);
    //根据影院编号，获取影院信息
    CinemaInfoVO getCinemaInfoById(int cinemaId);
    //获取所有电影的信息和对应的放映场次信息，根据影院编号
    FilmInfoVO getFilmInfoByCinemaId(int cinemaId);
    //根据放映场次获取放映信息
    FilmFieldVO getFilmFieldInfo(int fileldId);
    //根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    FilmInfoVO getFilmInfoByFieldId(int fieldId);
}
