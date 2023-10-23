package com.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.company.DAO.userRepository;
import com.company.entities.User;
import com.company.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	/*
	@Autowired
	private userRepository userRepository;
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User user = new User();
		user.setName("Rajan");
		user.setEmail("singh@gmail.com");
		Contact contact = new Contact();
		user.getContacts().add(contact);
		userRepository.save(user);
		return "Working";
	}
	*/
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private userRepository userRepository;
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title","Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
	@PostMapping("/do_register")
	public String registerUser(@RequestParam(value = "agreement", defaultValue ="false") boolean agreement,@ModelAttribute("user")User user, Model model, HttpSession session) {
		
		try {
			
			user.setEnabled(true);
			user.setRole("ROLE_USER");
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("User"+ user);
			User result=this.userRepository.save(user);
			model.addAttribute("user", new User());
			//session.setAttribute("message", new Message("Successfully registered!! ","alert-success"));
			return "home";

			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
}
