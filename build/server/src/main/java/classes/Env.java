package classes;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Env {
  public static Env INS;

  @Value("${d3e.baseHttpUrl:http://127.0.0.1:8080}")
  private String baseHttpUrl;

  @Value("${d3e.baseWSurl:ws://127.0.0.1:8080}")
  private String baseWSurl;

  @PostConstruct
  public void assign() {
    INS = this;
  }

  public static Env get() {
    return INS;
  }

  public String getBaseHttpUrl() {
    return this.baseHttpUrl;
  }

  public String getBaseWSurl() {
    return this.baseWSurl;
  }
}
