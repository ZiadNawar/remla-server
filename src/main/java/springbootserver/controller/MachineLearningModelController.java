package springbootserver.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springbootserver.data.MachineLearningModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Controller
@RequestMapping(path = "/")
public class MachineLearningModelController {
    private String modelHost;
    private RestTemplateBuilder rest;
    private int http_requests_total = 0;
    private ArrayList<Integer> avg = new ArrayList<>();

    public MachineLearningModelController(RestTemplateBuilder rest, Environment env) {
        this.rest = rest;
        modelHost = env.getProperty("MODEL_HOST");
    }

    @GetMapping("/")
    public String index(Model m) {
        m.addAttribute("hostname", modelHost); // to make it easier to see the model host
        return "model/index";
    }

    @GetMapping(value="/metrics",produces = "text/plain")
    @ResponseBody
    public String metrics(){
        StringBuilder sb = new StringBuilder();

        sb.append("# HELP http_requests_total The total number of HTTP requests.\n");
        sb.append("# TYPE http_requests_total counter\n");
        sb.append("http_requests_total ").append(http_requests_total).append("\n\n");

        double sum = 0;
        for(int d : avg) sum += d;
        double res = 0;
        if (avg.size()!=0){
            res = sum/avg.size();
        } else {
            res = 0;
        }
        sb.append("# HELP average_size_input The average size of text input.\n");
        sb.append("# TYPE average_size_input counter\n");
        sb.append("average_size_input ").append(res).append("\n\n");

        return sb.toString();
    }

    @PostMapping
    @ResponseBody
    public MachineLearningModel predict(@RequestBody MachineLearningModel model) {
        addRequest();
        addWord(model.input_data);
        model.result = getPredication(model);

        return model;
    }

    private String getPredication(MachineLearningModel model) {
        try {
            URI url = new URI(modelHost + "/predict");
            ResponseEntity<MachineLearningModel> c = rest.build().postForEntity(url, model, MachineLearningModel.class);
            StringBuilder str = new StringBuilder();


            if ( c.getBody().result_tfidf.length == 0 ){
                str.append("TF IDF model couldn't classify your input");
            } else {
                str.append("TF_IDF model result\n");
                str.append(String.join(" ,", c.getBody().result_tfidf));

            }
            str.append("\n");
            if ( c.getBody().result_mybag.length == 0 ){
                str.append("My Bag model couldn't classify your input");
            } else {
                str.append("My Bag model result \n");
                str.append(String.join(" ,", c.getBody().result_mybag));

            }

            System.out.println(str.toString());
            return str.toString().trim();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void addRequest(){
        this.http_requests_total += 1;
    }

    private void addWord(String text){
        this.avg.add(text.length());
    }
}
