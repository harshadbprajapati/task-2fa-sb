package com.tsv.implementation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tsv.implementation.dao.UserRepository;
import com.tsv.implementation.dto.UserLoginDTO;
import com.tsv.implementation.model.User;
import com.tsv.implementation.service.DefaultUserService;



@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private DefaultUserService userService;
	
	@Autowired
	UserRepository userRepo;
    
    @ModelAttribute("user")
    public UserLoginDTO userLoginDTO() {
        return new UserLoginDTO();
    }
    
	@GetMapping	//Redirect to login
	public String login()
	{
		return "login";
	}
	
	@PostMapping		//after submit will input will have mail and password
	public void loginUser(@ModelAttribute("user") UserLoginDTO userLoginDTO)
	{
		System.out.println("UserDTO"+userLoginDTO);
		 userService.loadUserByUsername(userLoginDTO.getUsername());	//Searches the mail from the repository
	}	//Defined in DefaultUserImpl

	@GetMapping("/otpVerification")	//We will be redirected to otp screen
	public String otpSent(Model model,UserLoginDTO userLoginDTO) {
		model.addAttribute("otpValue", userLoginDTO);
		return "otpScreen";
	}
	@PostMapping("/otpVerification")
	public String otpVerification(@ModelAttribute("otpValue") UserLoginDTO userLoginDTO) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();
		User users = userRepo.findByEmail(user.getUsername());
		if(users.getOtp() == userLoginDTO.getOtp())	//Verifying the otp
		return "redirect:/dashboard";			//Returning to the dashboard
		else
			return "redirect:/login/otpVerification?error";
	}
	
}
