package com.company.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.DAO.ContactRepository;
import com.company.DAO.userRepository;
import com.company.entities.Contact;
import com.company.entities.User;
import com.company.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private userRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
		
	@ModelAttribute
	public void addCommomData(Model model, Principal principal) {
		String userName= principal.getName();
		//System.out.println(" Your name "+userName);
	User user =	userRepository.getUserByUserName(userName);
	//System.out.println("USER "+ user);
	model.addAttribute("user",user);
	}
	@RequestMapping("/index")
	public String dashborad(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard - Smart Contact Manager");

		return "dashboard";
	}
	@GetMapping("/addContact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "addContact";
	}
	
	@PostMapping("/processContact")
	public String processContact(@ModelAttribute Contact contact, Principal principal, HttpSession session){
		
		 try {
			 String name = principal.getName();
			 User user = this.userRepository.getUserByUserName(name);
			 contact.setUser(user);
			 user.getContacts().add(contact);
			 this.userRepository.save(user);
			//System.out.println("Data " + contact);
			 session.setAttribute("message", new Message("Your contact is added!! add new one...","succes"));
			
		} catch (Exception e) {
			e.printStackTrace();
			 session.setAttribute("message", new Message("Something went wrong! Try again..","danger"));

			
		}
		return "addContact";
	} 
	@GetMapping("/showContacts/{page}")
	public String showContacts(@PathVariable("page")Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show Contacts - Smart Contact Manager");
		String name = principal.getName();
		 User user = this.userRepository.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts= this.contactRepository.findContactsByUserContacts(user.getId(),pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		return "showContacts";
	}
	@RequestMapping("/{cId}/showContacts")
	public String showContactDetails(@PathVariable("cId")Integer cId, Model model,Principal principal) {
		//System.out.println("cId "+ cId);
	Optional<Contact> contactoptional=this.contactRepository.findById(cId);
	Contact contact = contactoptional.get();
	//model.addAttribute("contact", contact);
		String userName = principal.getName();
	User user=this.userRepository.getUserByUserName(userName);
	if (user.getId()==contact.getUser().getId()) {
		model.addAttribute("contact", contact);
		model.addAttribute("title", contact.getName());
		
	}
		return "showContactsDetails";
	}
	@GetMapping("/delete/{cId}")
	public String delete(@PathVariable("cId") Integer cId, Model model,HttpSession session,Principal principal) {
		
		Contact contact = this.contactRepository.findById(cId).get();
		//contact.setUser(null);
		//this.contactRepository.delete(contact);
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
		session.setAttribute("message", new Message("Contact deleted successfully..","success"));
		return "redirect:/user/showContacts/0";
	}
	@PostMapping("/updateContact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Model model) {
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		model.addAttribute("title", "Update Contact");
		return "updateContact";
	}
	@PostMapping("/processUpdate")
	public String processUpdate(@ModelAttribute Contact contact, Model model, HttpSession session, Principal principal) {
		try {
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact updated successfully..","success"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getcId()+"/showContacts";
	}
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title","Profile");
		return "profile";
	}
	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("title","Settings");
		return "settings";
	}
	@PostMapping("/changePassword")
	public String changePassword(Model model, @RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal principal,HttpSession session) {
		model.addAttribute("title","Change-Password");
		//System.out.println("Old Password "+ oldPassword);
		//System.out.println("New Password "+ newPassword);
		String userName= principal.getName();
		User currentUser =  this.userRepository.getUserByUserName(userName);
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password updated successfully..","success"));

		}else {
			session.setAttribute("message", new Message("Please enter correct old password...","danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
}
