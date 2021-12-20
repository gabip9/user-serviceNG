package com.kaltura.userserviceNG;


//import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaltura.userserviceNG.bean.Login;
import com.kaltura.userserviceNG.bean.Register;

@SpringBootTest(classes = UserServiceNgApplication.class)
public class UserServiceNgApplicationTests  extends AbstractTestNGSpringContextTests {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired 
	private ObjectMapper mapper;
	private String userName;
	private String password;

	private MockMvc mockMvc;
	
	// some test life-cycle annotations (for internal use): 

	@BeforeClass
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); // environment preparation
	}	
	
	@BeforeTest
	public void setupBeforeTest() {
		logger.info(" ******  ");
		logger.info("starting the test..."); // variables preparation. 	
		logger.info(" ******  ");
	}
	
	@BeforeMethod
	public void setupBeforeMethod() {
		logger.info(" ******  "); 
		logger.info("before method: Activating required methods ..."); 
		logger.info(" ******  ");
	}

	@AfterTest
	public void setupAfterTest() {
		logger.info(" ******  ");
		logger.info("The test is ended"); // clean up the used variables.
		logger.info(" ******  ");
	}
	
	@AfterClass
	public void setupAfterClass() {
		logger.info(" ******  ");
		logger.info("after class was activated..."); 
		logger.info(" ******  ");
	}
	
	@AfterMethod
	public void setupAfterMethod() {
		logger.info(" ******  ");
		logger.info("after method: ending the method...");	
		logger.info(" ******  ");
	}

	
	@Test(priority = 1)
	public void testRegisterUser() throws URISyntaxException, StreamReadException, DatabindException, IOException , Exception{
				
		 logger.info("start testRegisterUser");
		
		 mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // for any extra datamembers (properties)
		 Register register = mapper.readValue(new File("src/test/resources/json_register.json"), Register.class);
		 userName = register.getUser().getUsername();
		 password = register.getPassword();
		 String json = mapper.writeValueAsString(register);
		
		 ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders.post("/register")
				  .content(json)
				  .contentType(MediaType.APPLICATION_JSON)
				  .accept(MediaType.APPLICATION_JSON));
		 
		 MvcResult result = resultActions.
		 	andExpect(status().isOk()).
		 	andExpect(content().contentType(MediaType.APPLICATION_JSON)).
		 	// might be without comments in-real world use (with proper response of the server with result.id and country.id properties):
		 	//andExpect(jsonPath("$.result.id").exists()).andExpect(jsonPath("$.result.id").isString()).
		 	//andExpect(jsonPath("$.countryId").exists()).andExpect(jsonPath("$.countryId").isNumber()).
		 	andExpect(jsonPath("$.result.error.code").isString()).
		 	andDo(print()).
		 	andReturn();
		 
		 // another option
		 Assert.assertNotNull( result.getResponse().getHeader("Content-Type"));		 
		 
		 
		 // print error message - for practice purpose		 
		 // since the registration fails - we catch the error and print it
		 String res = result.getResponse().getContentAsString(); 
		 register = mapper.readValue(res, Register.class);
		 logger.info(register.getResult().getError().getMessage());
			  
			 		
		 logger.info("end testRegisterUser");
	}
	
	@Test(priority = 2)
	public void testLoginUser() throws URISyntaxException, StreamReadException, DatabindException, IOException , Exception{
		
		 logger.info("start testLoginUser");
		
		 mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // for any extra datamembers (properties)
		 Login login = mapper.readValue(new File("src/test/resources/json_login.json"), Login.class);
		 login.setUsername(userName);
		 login.setPassword(password);
		 
		 String json = mapper.writeValueAsString(login);
		
		 ResultActions resultActions =  mockMvc.perform(MockMvcRequestBuilders.post("/login")
				  .content(json)
				  .contentType(MediaType.APPLICATION_JSON)
				  .accept(MediaType.APPLICATION_JSON));
		 
		 MvcResult result = resultActions.
		 	andExpect(status().isOk()).
		 	andExpect(content().contentType(MediaType.APPLICATION_JSON)).
			// might be without comments in-real world use (with proper response of the server with lastLoginDate property):
		 	//andExpect(jsonPath("$.result.user.lastLoginDate").exists()).
		 	// and check date validity
		 	andDo(print()).
		 	andReturn();
		 		 
		 String res = result.getResponse().getContentAsString();
		 JSONObject jsonObject = new JSONObject(res);
		 
		 // should be uncommented in-real world use (with proper response of the server with lastLoginDate property):
		 //long lastLoginDate = jsonObject.getJSONObject("result").getJSONObject("user").getLong("lastLoginDate");
		 // assertion if “lastLoginDate” exists
		 //Assert.assertNotNull(lastLoginDate);
		 
			
		/* should be uncommented in-real world use (with proper response of the server with lastLoginDate property):
		 * Date date = new Date(lastLoginDate);
		 * Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		 * String dateString = format.format(date);
		 * Assert.assertNotNull(dateString);
		 */
			 
		 
	     // logger.info(dateString);
		 

		 // should be uncommented in-real world use (with proper response of the server with login success , after the registration success):
		 // print error message 		 
			/*
			 * String res = result.getResponse().getContentAsString(); 
			 * register = mapper.readValue(res, Register.class);
			 * logger.info(register.getResult().getError().getMessage());
			 */
			 		 		 
		 logger.info("end testLoginUser");
	}

}
