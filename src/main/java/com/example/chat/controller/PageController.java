package com.example.chat.controller;

import com.example.chat.entity.ChatRoom;
import com.example.chat.security.CustomUserDetails;
import com.example.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";  // /login 페이지로 리다이렉트
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.html 템플릿 반환
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";  // signup.html 템플릿 반환
    }

    @GetMapping("/chat")
    public String chatListPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "redirect:/login"; // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
        }

        model.addAttribute("username", userDetails.getUsername());  // 로그인한 사용자 이름을 모델에 추가하여 HTML로 전달
        model.addAttribute("rooms", chatRoomService.getChatRoomForUser(userDetails.getId()));

        return "chatlist"; // 채팅방 리스트 화면으로 이동
    }

    @GetMapping("/chat/{roomId}")
    public String enterChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (userDetails == null) {
            return "login";
        }

        model.addAttribute("username", userDetails.getUsername());
        Optional<ChatRoom> chatRoomOpt = chatRoomService.getChatRoomById(roomId); // 채팅방 객체 조회
        if (chatRoomOpt.isPresent()) {
            model.addAttribute("chatRoom", chatRoomOpt.get()); // .get 을 붙인 이유 : Optional 로 받은 객체이기때문에, 값이 있을때만 받아온다. (isPresent와 이중체크)
        } else {
            return "redirect:/chat"; // redirect 는 HTTP GET 요청으로 리다이렉트 한다. 만약 DeleteMapping("/chat") 이 있어도 그쪽으로 가지않음.
        }

        return "chatRoom";
    }
}
