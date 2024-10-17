package org.c4marathon.assignment.mail.dto;

import org.c4marathon.assignment.user.domain.User;

public record UserInfoDto (String email, String nickname) {

	static UserInfoDto from(User user) {
		return new UserInfoDto(user.getEmail(), user.getNickname());
	}
}
