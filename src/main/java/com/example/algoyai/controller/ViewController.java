package com.example.algoyai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

	@GetMapping("/ai/chat")
	public String index() {
		return "index";
	}
}