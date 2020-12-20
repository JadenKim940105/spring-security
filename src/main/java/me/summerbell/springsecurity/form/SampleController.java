package me.summerbell.springsecurity.form;

import me.summerbell.springsecurity.account.AccountContext;
import me.summerbell.springsecurity.account.AccountRepository;
import me.summerbell.springsecurity.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.concurrent.Callable;

@Controller
public class SampleController {

    @Autowired
    SampleSerivce sampleSerivce;

    @Autowired
    AccountRepository accountRepository;


    @GetMapping("/")
    public String index(Model model, Principal principal){
        if(principal == null)
        model.addAttribute("message", "Hello Spring Security");
        else{
            model.addAttribute("message", "Hello, " + principal.getName());
        }
        return "index";
    }

    @GetMapping("/info")
    public String info(Model model){
        model.addAttribute("message", "Info");
        return "info";
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal){
        model.addAttribute("message", "Dashboard, " + principal.getName());
        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleSerivce.dashboard();
        return "dashboard";
    }
    @GetMapping("/admin")
    public String admin(Model model, Principal principal){
        model.addAttribute("message", "Admin, " + principal.getName());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal){
        model.addAttribute("message", "Admin, " + principal.getName());
        return "user";
    }

    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> aysncHanlder(){
        SecurityLogger.log("MVC");
        /* callable 을 사용하면 call() 을 처리하기전에 Request 를 처리하던 thread 를 반환한다.
           그리고 call() 의 동작이 완료되면 응답을 보내준다.
         */
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                SecurityLogger.log("Callable");
                return "Aysnc Handler";
            }
        };
    }


    @GetMapping("/async-service")
    @ResponseBody
    public String asyncServce(){
        SecurityLogger.log("MVC, before async service");
        sampleSerivce.asyncService();
        SecurityLogger.log("MVC, after async service");
       return "Async Service";
    }



}

