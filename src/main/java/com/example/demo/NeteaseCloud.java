package com.example.demo;

import com.google.gson.Gson;
import com.example.utils.HttpConnectionUtil;
import com.example.vo.SongListVo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 抓取网易云音乐歌单的单曲
 */
public class NeteaseCloud {

    //static String songListArr[] = {"106187651","2309892129","2155940594","2155941550","2155990346","2613993834","2155973107"};
    //static String songListArr[] = {"2613993834"};

    public final static Map<String,String> songListMap = new LinkedHashMap();
    static {

        //nationalSongList
        songListMap.put("2995113525", "国庆歌单");
        //songListMap.put("106187651", "喜欢音乐");
        //songListMap.put("2309892129", "热门音乐" );
        //songListMap.put("2155940594", "动感音乐" );
        //songListMap.put("2155941550", "安静音乐" );
        //songListMap.put("2155990346", "深情慢歌" );
        //songListMap.put("2613993834", "很酷英文" );
        //songListMap.put("2155973107", "跑步音乐" );
    }

    static List songIds = new ArrayList<>();
    static List songNames = new ArrayList<>();
    static List songTypes = new ArrayList<>();
    static List artistNames = new ArrayList<>();
    static List albumCovers = new ArrayList<>();
    static List albumNames = new ArrayList<>();



    public static void main(String[] args) throws Exception {

        List<SongListVo> list = new ArrayList<> ();

        //全部歌单
        //getSongList(false,list);

        //每个歌单
        getSongList(true,list);


    }

    /**
     * 遍历歌单
     */
    private static void getSongList(Boolean falg,List<SongListVo> list) throws Exception {

        SongListVo allVo = new SongListVo();

        for (Map.Entry<String,String> entry : songListMap.entrySet()) {
            String songListId = entry.getKey();
            String songListName = entry.getValue();

            getSongId(songListId);
            SongListVo vo = new SongListVo();
            vo.setSongSheetName(songListName);
            vo.setAuthor("狂欢马克思");
            vo.setSongIds(songIds);
            vo.setSongNames(songNames);
            vo.setSongTypes(songTypes);
            vo.setArtistNames(artistNames);
            vo.setAlbumCovers(albumCovers);
            vo.setAlbumNames(albumNames);

            if (falg){
                songIds = new ArrayList<>();
                songNames = new ArrayList<>();
                songTypes = new ArrayList<>();
                artistNames = new ArrayList<>();
                albumCovers = new ArrayList<>();
                albumNames = new ArrayList<>();

                list.add(vo);
            }else{
                BeanUtils.copyProperties(vo,allVo);
            }

        }

        if (!falg){
            allVo.setSongSheetName("全部歌单");
            list.add(allVo);
        }

        String string = new Gson().toJson(list);
        System.out.println(string);
    }

    /**
     * 获取歌单里的所有单曲
     * @param songListId
     * @throws Exception
     */
    private static void getSongId(String songListId) throws Exception {
        Connection d = Jsoup.connect("http://music.163.com/playlist?id=" + songListId)
                .header("Referer", "http://music.163.com/")
                .header("Host", "music.163.com")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");

        Elements dd = d.get().select("ul[class=f-hide] a");

        dd.stream().filter(w -> Objects.nonNull(w)).forEach(w -> {

            String songName = w.text();
            String songId = w.attr("href").replace("/song?id=", "");

            songIds.add(songId);
            songNames.add(songName);
            songTypes.add("wy");
            artistNames.add("狂欢马克思");
            albumNames.add("网易专辑");
            albumCovers.add("https://p2.music.126.net/PRewo_EHoTOA80YCScNevw==/109951163451769788.jpg?param=300x300");

            //下载音乐
            //downloadFile(songId,songName);
        });


    }

    /**
     * 下载文件
     */
    private static void downloadFile(String songId,String songName){
        // 下载文件测试
        String downloadFile = HttpConnectionUtil.downloadFile("http://music.163.com/song/media/outer/url?id="+songId, "F:\\music163\\"+songName+".mp3");
        System.out.println("网易云音乐下载返回的单曲路径：" + downloadFile);
        try {
            //等待下载
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
