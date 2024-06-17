package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.Tier;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("Oauth 유저 불러오기");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );



        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2UserInfo.getId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();



        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {

            //여기서 주소렁 닉네임 설정해줄 수 있게
            user = User.builder()
                    .userName(name)
                    .tier(Tier.BRONZE)
                    .totalScore(0)
                    .isDone(true)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .role(Role.ROLE_USER)
                    .build();
            userRepository.save(user);
        }

        PrincipalDetails principalDetails = new PrincipalDetails(user, oAuth2User.getAttributes());
        return principalDetails;
    }
}

