package com.a606.api.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NFTDto {
    private long id;
    private String tokenId;
    private boolean isSaled;
    private String name;
    private int likeCount;
}
