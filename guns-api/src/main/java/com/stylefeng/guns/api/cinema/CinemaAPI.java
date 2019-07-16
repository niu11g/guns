package com.stylefeng.guns.api.cinema;

import com.stylefeng.guns.api.cinema.vo.CinemaRequestVO;
import com.stylefeng.guns.api.cinema.vo.CinemaVO;


public interface CinemaAPI {
    //根据CinemaQueryVO,查询影院列表
    Page<CinemaVO> getCinemas(CinemaRequestVO cinemaRequestVO);
    //根据条件获取品牌列表

    //获取行政区域列表

    //获取影厅类型列表

    //根据影院编号，获取影院信息

    //获取所有电影的信息和对应的放映场次信息，根据影院编号

    //根据影院编号获取影院信息

    //根据放映场次获取放映信息

    //根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息

}
