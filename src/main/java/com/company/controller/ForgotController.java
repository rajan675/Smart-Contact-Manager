package com.company.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.DAO.userRepository;
import com.company.entities.User;
import com.company.sevice.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	Random random = new Random(100000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private userRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@RequestMapping("/forgot")
	public String openForm() {
		return "forgotForm";
	}
	
	@PostMapping("/sendOTP")
	public String sendOTP(@RequestParam("email")String email, HttpSession session) {
		
		System.out.println("Email  "+email);
		int otp = random.nextInt(999999);
		System.out.println("OTP " +otp);
		String subject = "OTP from Smart Contact Manager";
		String message = " OTP ="+otp+"";
		String to =email ;
		 boolean flag = this.emailService.sendEmail(subject, message, to);
		 if (flag) {
			 session.setAttribute("otp",otp);
			 session.setAttribute("email", email);
			return "verifyOTP";
		}else {
			session.setAttribute("message", "Check your email id!...");
			return"forgotForm";
		} 
	}
	@PostMapping("/verifyOTP")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {
		
		int myOtp =(int) session.getAttribute("otp");
		String email = (String)session.getAttribute("email");
		if (myOtp==otp) {
			User user = this.userRepository.getUserByUserName(email);
			if (user==null) {
				session.setAttribute("message", "User does not exist with this email...");
				return "forgotForm";
			}else {
				
			}
			return "changePassword";
		}else {
			session.setAttribute("message","You have entered wrong OTP");
			return "verifyOTP";
		}
		
	}
	@PostMapping("/changePassword")
	public String changePassword(@RequestParam("newPassword")String newPassword, HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change= Password changed successfully...";
	}
}
