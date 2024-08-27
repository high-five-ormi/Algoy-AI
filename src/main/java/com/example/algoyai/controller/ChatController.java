package com.example.algoyai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ai")
public class ChatController {

	@GetMapping("/chat")
	public String chat() {
		return "chat";
	}
}