// package application.inventory;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// import java.util.List;
// import java.util.Map;
//
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.test.context.junit4.SpringRunner;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import application.inventory.controller.InventoryController;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"MYSQL_HOST=host.docker.internal", "MYSQL_PORT=3306", "MYSQL_DATABASE=inventorydb", "MYSQL_USER=dbuser", "MYSQL_PASSWORD=password"})
// public class ActuatorTest {
//
//     @Autowired
//     private TestRestTemplate restTemplate;
//
//     @MockBean
//     private InventoryController inventoryController;
//
//     @Test
//     public void contextLoads() throws Exception {
//         assertThat(inventoryController).isNotNull();
//     }
//     @Test
//     public void testHealthEndpoint() {
//         System.out.println("testing health end point...");
//         ResponseEntity<String> entity = this.restTemplate.getForEntity("/actuator/health", String.class);
//         assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(entity.getBody()).contains("\"status\":\"UP\"");
//     }
//
//     @Test
//     public void testLivenessEndpoint() {
//         System.out.println("testing liveness end point...");
//         ResponseEntity<String> entity = this.restTemplate.getForEntity("/actuator/liveness", String.class);
//         assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(entity.getBody()).contains("\"status\":\"UP\"");
//     }
//
//     @Test
//     @SuppressWarnings("unchecked")
//     public void testMetricsEndpoint() {
//         testLivenessEndpoint(); // access a page
//         System.out.println("testing metrics end point...");
//         @SuppressWarnings("rawtypes")
//         ResponseEntity<Map> entity = this.restTemplate.getForEntity("/actuator/metrics", Map.class);
//         assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//         Map<String, Object> body = entity.getBody();
//         assertThat(body).containsKey("names");
//         assertThat((List<String>) body.get("names")).contains("jvm.buffer.count");
//     }
//
//     @Test
//     public void testPrometheusEndpoint() {
//         testLivenessEndpoint(); // access a page
//         System.out.println("testing prometheus end point...");
//         ResponseEntity<String> entity = this.restTemplate.getForEntity("/actuator/prometheus", String.class);
//         assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
//         assertThat(entity.getBody()).contains("# TYPE jvm_buffer_count_buffers gauge");
//     }
//
// }
