package com.a606.api.service;

import com.a606.db.entity.NFT;
import com.a606.db.repository.BoardRepository;
import com.a606.db.repository.NFTRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("boardService")
public class BoardServiceImpl implements BoardService{

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    NFTRepository nftRepository;

    @Override
    public List<NFT> findNFTList(Long id) {
        return null;
    }

    @Override
    public List<?> getBoardList(String tab, String order, int asc, int pageNo, int npp) {
        // query문 짜기
        return null;
    }
}
