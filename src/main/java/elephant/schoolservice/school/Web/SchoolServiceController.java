package elephant.schoolservice.school.Web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Api("StudentServiceController")
public class SchoolServiceController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/getSchoolDetails/{schoolname}", method = RequestMethod.GET)
    @ApiOperation(value = "getStudents", notes = "查找学生")
    @HystrixCommand(fallbackMethod = "hiError")
    public String getStudents(RequestEntity<String> requestEntity, @PathVariable String schoolname)
    {
        System.out.println("Getting School details for " + schoolname);

        ResponseEntity<String>  responseEntity=restTemplate.exchange("http://student-service/getStudentDetailsForSchool/{schoolname}",
                HttpMethod.GET, null, new ParameterizedTypeReference<String>() {}, schoolname);

        HttpHeaders httpHeaders= responseEntity.getHeaders();
        String service= httpHeaders.getFirst("X-Application-Context");
        String response = responseEntity.getBody();

        System.out.println("Response Received as " + service+" "+response);
        String schoolService=requestEntity.getHeaders().getFirst("host");

        return "School Name -  " + schoolname + " \n Student Details " + response+"\n School Service IP:"+schoolService+ " \n Student Service IP:"+service;
    }

    public String hiError(RequestEntity<String> requestEntity, @PathVariable String schoolname)
    {
        return "Hi Error.";
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
