package com.a606.api.controller;

import com.a606.api.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(value = "Board API", tags = {"Board."})
@RestController
@RequestMapping("/api/board")
public class BoardController {

    @Autowired
    BoardService boardService;

    @GetMapping()
    @ApiOperation(value = "NFT 목록 조회",
            notes = "탭(전체/분양중/미분양), 정렬기준(좋아요순/등록순), 정렬순서(높은순/낮은순), 페이지 번호, 페이지 당 NFT 수 로 NFT 목록을 조회한다.")
    public List<?> searchAllList(@RequestParam Long id) {
        return boardService.findNFTList(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "NFT 목록 조회",
            notes = "탭(전체/분양중/미분양), 정렬기준(좋아요순/등록순), 정렬순서(높은순/낮은순), 페이지 번호, 페이지 당 NFT 수 로 NFT 목록을 조회한다.")
    public List<?> searchList(@RequestParam String tab, @RequestParam String order, @RequestParam int asc, @RequestParam int pageNo, @RequestParam int npp) {
        return boardService.getBoardList(tab, order, asc, pageNo, npp);
    }

}
