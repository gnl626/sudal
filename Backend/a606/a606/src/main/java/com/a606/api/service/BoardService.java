package com.a606.api.service;

import com.a606.db.entity.NFT;

import java.util.List;

public interface BoardService {

    List<NFT> findNFTList(Long id);
    List<?> getBoardList(String tab, String order, int asc, int pageNo, int npp);
}
