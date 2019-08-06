package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.api.film.FilmAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionList;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmAPI.class,check=false)
    private FilmAPI filmServiceApi;

    /*
      API网关：
        1.功能聚合[API聚合]
        好处：
            1.六个接口，一次请求，同一时刻节省了五次Http请求
            2.同一个接口对外暴露，降低了前后端分离开发的难度和复杂度
        坏处：
            1.一次获取数据太多，容易出现问题
     */
    //获取首页接口信息
    @RequestMapping(value="getIndex",method= RequestMethod.GET)
    public ResponseVO<FilmIndexVO> getIndex(){
//        BannersVO bannersVO = new BannersVO();
//        bannersVO.setBannerAddress("");
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        //获取banner信息
        filmIndexVO.setBanners(filmServiceApi.getBanners());
        //获取正在热映的电影
        filmIndexVO.setHotFilms(filmServiceApi.getHotFilms(true,8,1,1,99,99,99));
        //即将上映映的电影
        filmIndexVO.setSoonFilms(filmServiceApi.getSoonFilms(true,8,1,1,99,99,99));
        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceApi.getBoxRanking());
        //获取受欢迎的榜单
        filmIndexVO.setExpectRanking(filmServiceApi.getExpectRanking());
        //获取前一百
        filmIndexVO.setTop100(filmServiceApi.getTop100());

        return ResponseVO.success(IMG_PRE,filmIndexVO);
    }

    //影片条件列表查询接口
    @RequestMapping(value="getConditionList",method=RequestMethod.GET)
    public ResponseVO<FilmConditionList> getConditionList(@RequestParam(name = "catId",required = false,defaultValue = "99")String catId,
                                                          @RequestParam(name = "sourceId",required = false,defaultValue = "99")String sourceId,
                                                          @RequestParam(name = "yearId",required = false,defaultValue = "99")String yearId){
        FilmConditionList filmConditionList = new FilmConditionList();
        List<CatInfoVO> catInfos = filmServiceApi.getCatInfo();
        for(CatInfoVO catInfo : catInfos){
            if(catInfo.getCatId().equals(catId)){
                catInfo.setActive(true);
            }
        }
        filmConditionList.setCatInfo(catInfos);
        List<SourceInfoVO> sourceInfos = filmServiceApi.getSourceInfo();
        for(SourceInfoVO sourceInfo : sourceInfos){
            if(sourceInfo.getSourceId().equals(sourceId)){
                sourceInfo.setActive(true);
            }
        }
        filmConditionList.setSourceInfo(sourceInfos);
        List<YearInfoVO> yearInfos = filmServiceApi.getYearInfo();
        for(YearInfoVO yearInfo : yearInfos){
            if(yearInfo.getYearId().equals(yearId)){
                yearInfo.setActive(true);
            }
        }
        filmConditionList.setYearInfo(yearInfos);
        return ResponseVO.success(filmConditionList);
    }
    //影片条件列表查询接口
    @RequestMapping(value="getFilms",method=RequestMethod.GET)
    public ResponseVO<FilmVO> getFilms(FilmRequestVO requestVO){
        FilmVO filmVO = null;
        //根据showType判断影片查询类型
        switch(requestVO.getShowType()){
            case 1:
                filmVO = filmServiceApi.getHotFilms(false,requestVO.getPageSize(),requestVO.getNowPage(),
                        requestVO.getSortId(),requestVO.getSourceId(),requestVO.getYearId(),requestVO.getCatId());
                break;
            case 2:
                filmVO = filmServiceApi.getSoonFilms(false,requestVO.getPageSize(),requestVO.getNowPage(),
                        requestVO.getSortId(),requestVO.getSourceId(),requestVO.getYearId(),requestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceApi.getClassFilms(requestVO.getPageSize(),requestVO.getNowPage(),
                        requestVO.getSortId(),requestVO.getSourceId(),requestVO.getYearId(),requestVO.getCatId());
                break;
            default:
                filmVO = filmServiceApi.getHotFilms(false,requestVO.getPageSize(),requestVO.getNowPage(),
                        requestVO.getSortId(),requestVO.getSourceId(),requestVO.getYearId(),requestVO.getCatId());
                break;
        }
        return ResponseVO.success(filmVO.getNowPage(),filmVO.getTotalPage(),IMG_PRE,filmVO.getFilmInfo());

    }

    //影片条件列表查询接口
    @RequestMapping(value="films/{searchParam}",method=RequestMethod.GET)
    public ResponseVO films(@PathVariable("searchParam")String searchParam,
                            int searchType){
        //根据searchType,判断查询类型
        FilmDetailVO filmDetail = filmServiceApi.getFilmDetails(searchParam, searchType);
        //不同的查询类型，传入的条件会略有不同
        String filmId = filmDetail.getFilmId();
        //查询影片的详细信息 > Dubbo的异步获取
        //获取影片描述信息
        FilmDescVO filmDescVO = filmServiceApi.getFilmDesc(filmId);
        //获取图片信息
        ImgVO imgVO = filmServiceApi.getImgs(filmId);
        //获取演员信息
        ActorVO directorVO = filmServiceApi.getDectInfo(filmId);
        List<ActorVO> actors = filmServiceApi.getActors(filmId);
        InfoRequestVO infoRequestVO = new InfoRequestVO();

        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setActors(actors);
        actorRequestVO.setDirector(directorVO);

        infoRequestVO.setBiography(filmDescVO.getBiography());
        infoRequestVO.setFilmId(filmDescVO.getFilmId());
        infoRequestVO.setActors(actorRequestVO);
        infoRequestVO.setImgs(imgVO);

        filmDetail.setInfo04(infoRequestVO);
        return ResponseVO.success(IMG_PRE,filmDetail);
    }
}
