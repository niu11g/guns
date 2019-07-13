package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = FilmAPI.class,loadbalance = "roundrobin")
public class FilmServiceAPI implements FilmAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;
    @Autowired
    private MoocFilmTMapper moocFilmTMapper;
    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;
    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;
    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;
    @Autowired
    private MoocFilmInfoTMapper moocFilmInfoTMapper;
    @Autowired
    private MoocActorTMapper moocActorTMapper;

    @Override
    public List<BannersVO> getBanners() {
        List<BannersVO> result = new ArrayList<>();
        List<MoocBannerT> moocBanners = moocBannerTMapper.selectList(null);
        for(MoocBannerT moocBannerT : moocBanners){
            BannersVO bannersVO = new BannersVO();
            bannersVO.setBannerId(moocBannerT.getUuid().toString());
            bannersVO.setBannerUrl(moocBannerT.getBannerUrl());
            bannersVO.setBannerAddress(moocBannerT.getBannerAddress());
            result.add(bannersVO);
        }
        return result;
    }

    private List<FilmInfoVO> getFilmInfos(List<MoocFilmT> moocFilms){
        List<FilmInfoVO> filmInfos = new ArrayList<>();
        for(MoocFilmT moocFilmT : moocFilms){
            FilmInfoVO filmInfo = new FilmInfoVO();
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));
            filmInfos.add(filmInfo);
        }
        return filmInfos;
    }

    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfoVO> filmInfos = new ArrayList<>();
        //判断是否是首页需要的内容
        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");
        if(isLimit){
            //如果是，则限制条数，限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(filmInfos);
        }else{
            //如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = null;
            //根据sortId的不同，来组织不同的Page对象
            //1 按热门搜索 2 按时间搜索 3 按评价搜索
            switch(sortId) {
                case 1:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_score");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_box_office");
                    break;
            }
            if(sourceId != 99){
                entityWrapper.eq("film_source",sourceId);
            }
            if(yearId != 99){
                entityWrapper.eq("film_date",yearId);
            }
            if(catId != 99){
                String catStr = "#"+catId+"#";
                entityWrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);
            //组织filminfos
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            //需要总页数 totalCounts/nums -> 0 + 1 = 1
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages = (totalCounts/nums) + 1;

            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfoVO> filmInfos = new ArrayList<>();
        //判断是否是首页需要的内容
        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","2");
        if(isLimit){
            //如果是，则限制条数，限制内容为即将上映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmInfo(filmInfos);
            filmVO.setFilmNum(moocFilms.size());
        }else{
            //如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = null;
            //根据sortId的不同，来组织不同的Page对象
            //1 按热门搜索 2 按时间搜索 3 按评价搜索
            switch(sortId) {
                case 1:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                case 2:
                    page = new Page<>(nowPage, nums, "film_time");
                    break;
                case 3:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
                default:
                    page = new Page<>(nowPage, nums, "film_preSaleNum");
                    break;
            }
            //如果sourceId,yearId,catId 不为99,则表示要按照对应的编号进行查询
            if(sourceId != 99){
                entityWrapper.eq("film_source",sourceId);
            }
            if(yearId != 99){
                entityWrapper.eq("film_date",yearId);
            }
            if(catId != 99){
                String catStr = "#"+catId+"#";
                entityWrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);
            //组织filminfos
            filmInfos = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            //需要总页数 totalCounts/nums -> 0 + 1 = 1
            int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
            int totalPages = (totalCounts/nums) + 1;

            filmVO.setFilmInfo(filmInfos);
            filmVO.setTotalPage(totalPages);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfoVO> filmInfos = new ArrayList<>();

        //热映影片的限制条件
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","3");

        //如果不是，则是列表页，同样需要限制内容为热映影片
        Page<MoocFilmT> page = null;
        switch(sortId) {
            case 1:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;
            case 2:
                page = new Page<>(nowPage, nums, "film_time");
                break;
            case 3:
                page = new Page<>(nowPage, nums, "film_score");
                break;
            default:
                page = new Page<>(nowPage, nums, "film_box_office");
                break;
        }
        //如果sourceId,yearId,catId 不为99,则表示要按照对应的编号进行查询
        if(sourceId != 99){
            entityWrapper.eq("film_source",sourceId);
        }
        if(yearId != 99){
            entityWrapper.eq("film_date",yearId);
        }
        if(catId != 99){
            String catStr = "#"+catId+"#";
            entityWrapper.like("film_cats",catStr);
        }
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);
        //组织filminfos
        filmInfos = getFilmInfos(moocFilms);
        filmVO.setFilmNum(moocFilms.size());
        //需要总页数 totalCounts/nums -> 0 + 1 = 1
        int totalCounts = moocFilmTMapper.selectCount(entityWrapper);
        int totalPages = (totalCounts/nums) + 1;

        filmVO.setFilmInfo(filmInfos);
        filmVO.setTotalPage(totalPages);
        filmVO.setNowPage(nowPage);
        return filmVO;
    }

    @Override
    public List<FilmInfoVO> getBoxRanking() {
        //条件-> 正在上映的，票房前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_box_office");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfoVO> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfoVO> getExpectRanking() {
        //条件-> 即将上映的，预售前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_preSaleNum");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfoVO> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfoVO> getTop100() {
        //条件-> 正在上映的，评分前10名
        EntityWrapper<MoocFilmT> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_score");
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page,entityWrapper);

        List<FilmInfoVO> filmInfos = getFilmInfos(moocFilms);
        return filmInfos;
    }

    @Override
    public List<CatInfoVO> getCatInfo() {
        List<CatInfoVO> result = new ArrayList<>();
        List<MoocCatDictT> moocCatDict = moocCatDictTMapper.selectList(null);
        for(MoocCatDictT moocCatDictT : moocCatDict){
            CatInfoVO catInfoVO = new CatInfoVO();
            catInfoVO.setCatId(moocCatDictT.getUuid().toString());
            catInfoVO.setCatName(moocCatDictT.getShowName());
            result.add(catInfoVO);
        }
        return result;
    }

    @Override
    public List<SourceInfoVO> getSourceInfo() {
        List<SourceInfoVO> result = new ArrayList<>();
        List<MoocSourceDictT> moocSourceDict = moocSourceDictTMapper.selectList(null);
        for(MoocSourceDictT moocSourceDictT : moocSourceDict){
            SourceInfoVO sourceInfoVO = new SourceInfoVO();
            sourceInfoVO.setSourceId(moocSourceDictT.getUuid().toString());
            sourceInfoVO.setSourceName(moocSourceDictT.getShowName());
            result.add(sourceInfoVO);
        }
        return result;
    }

    @Override
    public List<YearInfoVO> getYearInfo() {
        List<YearInfoVO> result = new ArrayList<>();
        List<MoocYearDictT> moocYearDict = moocYearDictTMapper.selectList(null);
        for(MoocYearDictT moocYearDictT : moocYearDict){
            YearInfoVO yearInfoVO = new YearInfoVO();
            yearInfoVO.setYearId(moocYearDictT.getUuid().toString());
            yearInfoVO.setYearName(moocYearDictT.getShowName());
            result.add(yearInfoVO);
        }
        return result;
    }

    @Override
    public FilmDetailVO getFilmDetails(String searchParam, int searchType) {
        // searcgType 1-按名称 2-按编号的查找
        FilmDetailVO filmDetailVO = null;
        if(searchType == 1){
            filmDetailVO = moocFilmTMapper.getFilmDetailByName("%"+searchParam+"%");
        }else{
            filmDetailVO = moocFilmTMapper.getFilmDetailById(searchParam);
        }
        return filmDetailVO;
    }


    private MoocFilmInfoT getFilmInfo(String filmId){
        MoocFilmInfoT moocFilmInfoT = new MoocFilmInfoT();
        moocFilmInfoT.setFilmId(filmId);

        moocFilmInfoT = moocFilmInfoTMapper.selectOne(moocFilmInfoT);

        return moocFilmInfoT;
    }

    @Override
    public FilmDescVO getFilmDesc(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);

        FilmDescVO filmDescVO = new FilmDescVO();
        filmDescVO.setBiography(moocFilmInfoT.getBiography());
        filmDescVO.setFilmId(filmId);
        return filmDescVO;
    }

    @Override
    public ImgVO getImgs(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);
        String filmImgStr = moocFilmInfoT.getFilmImgs();
        String[] filmImgs = filmImgStr.split(",");
        ImgVO imgVO = new ImgVO();
        imgVO.setMainImg(filmImgs[0]);
        imgVO.setImg01(filmImgs[1]);
        imgVO.setImg02(filmImgs[2]);
        imgVO.setImg03(filmImgs[3]);
        imgVO.setImg04(filmImgs[4]);
        return imgVO;
    }

    @Override
    public ActorVO getDectInfo(String filmId) {
        MoocFilmInfoT moocFilmInfoT = getFilmInfo(filmId);
        Integer directId = moocFilmInfoT.getDirectorId();
        MoocActorT moocActorT = moocActorTMapper.selectById(directId);
        ActorVO actorVO = new ActorVO();
        actorVO.setImgAddress(moocActorT.getActorImg());
        actorVO.setDirectorName(moocActorT.getActorName());
        return actorVO;
    }

    @Override
    public List<ActorVO> getActors(String filmId) {
        List<ActorVO> actors = moocActorTMapper.getActors(filmId);
        return actors;
    }
}
