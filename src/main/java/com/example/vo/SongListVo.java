package com.example.vo;

import java.util.List;

/**
 * @author Howe Hsiang
 * @date 2019/8/27 20:34
 **/
public class SongListVo {

    private List songIds;

    private List songNames;

    private String songSheetName;

    private List songTypes;

    private List albumCovers;

    private List albumNames;

    private List artistNames;

    private String author;


    public List getAlbumCovers() {
        return albumCovers;
    }

    public void setAlbumCovers(List albumCovers) {
        this.albumCovers = albumCovers;
    }

    public List getAlbumNames() {
        return albumNames;
    }

    public void setAlbumNames(List albumNames) {
        this.albumNames = albumNames;
    }

    public List getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List artistNames) {
        this.artistNames = artistNames;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List getSongIds() {
        return songIds;
    }

    public void setSongIds(List songIds) {
        this.songIds = songIds;
    }

    public List getSongNames() {
        return songNames;
    }

    public void setSongNames(List songNames) {
        this.songNames = songNames;
    }

    public String getSongSheetName() {
        return songSheetName;
    }

    public void setSongSheetName(String songSheetName) {
        this.songSheetName = songSheetName;
    }

    public List getSongTypes() {
        return songTypes;
    }

    public void setSongTypes(List songTypes) {
        this.songTypes = songTypes;
    }
}

