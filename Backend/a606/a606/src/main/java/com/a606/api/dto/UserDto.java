package com.a606.api.dto;

import com.a606.db.entity.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("User Model")
public class UserDto implements Serializable {


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info {
        @ApiModelProperty(name = "유저 id(식별자)", example = "1")
        private long id;

        private String nickname;

        private String wallet;

        private long gamePoint;

        private LocalDateTime createdTime;

        private LocalDateTime lastModifiedDate;

        private boolean isDelete = false;

        public Info(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.wallet = user.getWallet();
            this.gamePoint = user.getGamePoint();
            this.createdTime = user.getCreatedDate();
            this.lastModifiedDate = user.getLastModifiedDate();
            this.isDelete = user.isDelete();
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @ApiModelProperty(name = "지갑 주소")
        private String wallet;
        private String message;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateProfileRequest {
        @NotNull
        @ApiModelProperty(name="nft id", example = "1")
        private long nftId;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateNicknameRequest {
        @NotNull
        @ApiModelProperty(name="새 닉네임", example = "수달이가죽었다고")
        private String nickname;
    }

}
