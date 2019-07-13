package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmAPI {
    //获取banner信息
    List<BannersVO> getBanners();
    //获取正在热映的电影
    FilmVO getHotFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    //即将上映映的电影
    FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    //获取经典影片
    FilmVO getClassFilms(int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    //票房排行榜
    List<FilmInfoVO> getBoxRanking();
    //获取受欢迎的榜单
    List<FilmInfoVO> getExpectRanking();
    //获取前一百
    List<FilmInfoVO> getTop100();
    //影片类型列表
    List<CatInfoVO> getCatInfo();
    //影片国家列表
    List<SourceInfoVO> getSourceInfo();
    //影片年份列表
    List<YearInfoVO> getYearInfo();
    //根据影片ID或者名称获取影片信息
    FilmDetailVO getFilmDetails(String searchParam,int searchType);
    //获取影片相关的其他信息(演员表、图片地址)
    //获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);
    //获取图片信息
    ImgVO getImgs(String filmId);
    //获取导演信息
    ActorVO getDectInfo(String filmId);
    //获取演员信息
    List<ActorVO> getActors(String filmId);
}
