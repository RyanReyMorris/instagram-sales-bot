package ru.ryanreymorris.instagramsalesbot.devtools;

import ru.ryanreymorris.instagramsalesbot.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dev")
public class DevController {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private JobService jobService;

    @PostMapping("/stop")
    public void stopApp() {
        System.exit(SpringApplication.exit(context));
    }

    @PostMapping("/job/disable")
    public void deleteJobs() {
        jobService.disableAllJobs();
    }

    @PostMapping("/job/enable")
    public void enableJobs() {
        jobService.enableAllJobs();
    }
}
