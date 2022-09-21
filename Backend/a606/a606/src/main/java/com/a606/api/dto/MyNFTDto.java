package com.a606.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyNFTDto {
    private long id;
    private String tokenId;
    private String tokenURI;
    private boolean isSaled;
    private int likeCount;
    private String name;
}
