package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Article;
import edu.ucsb.cs156.example.repositories.ArticleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)
public class ArticlesControllerTests extends ControllerTestCase {

        @MockBean
        ArticleRepository articleRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/articles/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().is(200)); // logged
        }

        // @Test
        // public void logged_out_users_cannot_get_by_id() throws Exception {
        //         mockMvc.perform(get("/api/articles?id=7"))
        //                         .andExpect(status().is(403)); // logged out users can't get by id
        // }

        // Authorization tests for /api/articles/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/articles/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/articles/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

        //         // arrange
        //         LocalDateTime ldt = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBDate ucsbDate = UCSBDate.builder()
        //                         .name("firstDayOfClasses")
        //                         .quarterYYYYQ("20222")
        //                         .localDateTime(ldt)
        //                         .build();

        //         when(ucsbDateRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbDate));

        //         // act
        //         MvcResult response = mockMvc.perform(get("/api/articles?id=7"))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert

        //         verify(ucsbDateRepository, times(1)).findById(eq(7L));
        //         String expectedJson = mapper.writeValueAsString(ucsbDate);
        //         String responseString = response.getResponse().getContentAsString();
        //         assertEquals(expectedJson, responseString);
        // }

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

        //         // arrange

        //         when(ucsbDateRepository.findById(eq(7L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(get("/api/articles?id=7"))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert

        //         verify(ucsbDateRepository, times(1)).findById(eq(7L));
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("EntityNotFoundException", json.get("type"));
        //         assertEquals("UCSBDate with id 7 not found", json.get("message"));
        // }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbdates() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Article article1 = Article.builder()
                                .title("UCSB website")
                                .url("https://www.ucsb.edu")
                                .explanation("official UCSB website")
                                .email("mujia@ucsb.edu")
                                .dateAdded(ldt1)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                Article article2 = Article.builder()
                                .title("UCSB calendar")
                                .url("https://registrar.sa.ucsb.edu/calendars/academic-calendars")
                                .explanation("official UCSB calendar")
                                .email("mujia@ucsb.edu")
                                .dateAdded(ldt2)
                                .build();


                ArrayList<Article> expectedArticles = new ArrayList<>();
                expectedArticles.addAll(Arrays.asList(article1, article2));
                when(articleRepository.findAll()).thenReturn(expectedArticles);

                // act
                MvcResult response = mockMvc.perform(get("/api/articles/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(articleRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedArticles);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_article() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                Article article1 = Article.builder()
                                .title("UCSB-website")
                                .url("https://www.ucsb.edu")
                                .explanation("official-UCSB-website")
                                .email("mujia@ucsb.edu")
                                .dateAdded(ldt1)
                                .build();

                when(articleRepository.save(eq(article1))).thenReturn(article1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/articles/post?title=UCSB-website&url=https://www.ucsb.edu&explanation=official-UCSB-website&email=mujia@ucsb.edu&dateAdded=2022-01-03T00:00:00")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(articleRepository, times(1)).save(article1);
                String expectedJson = mapper.writeValueAsString(article1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_can_delete_a_date() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBDate ucsbDate1 = UCSBDate.builder()
        //                         .name("firstDayOfClasses")
        //                         .quarterYYYYQ("20222")
        //                         .localDateTime(ldt1)
        //                         .build();

        //         when(ucsbDateRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbDate1));

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         delete("/api/articles?id=15")
        //                                         .with(csrf()))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert
        //         verify(ucsbDateRepository, times(1)).findById(15L);
        //         verify(ucsbDateRepository, times(1)).delete(any());

        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBDate with id 15 deleted", json.get("message"));
        // }

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_tries_to_delete_non_existant_ucsbdate_and_gets_right_error_message()
        //                 throws Exception {
        //         // arrange

        //         when(ucsbDateRepository.findById(eq(15L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         delete("/api/articles?id=15")
        //                                         .with(csrf()))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert
        //         verify(ucsbDateRepository, times(1)).findById(15L);
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBDate with id 15 not found", json.get("message"));
        // }

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_can_edit_an_existing_ucsbdate() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
        //         LocalDateTime ldt2 = LocalDateTime.parse("2023-01-03T00:00:00");

        //         UCSBDate ucsbDateOrig = UCSBDate.builder()
        //                         .name("firstDayOfClasses")
        //                         .quarterYYYYQ("20222")
        //                         .localDateTime(ldt1)
        //                         .build();

        //         UCSBDate ucsbDateEdited = UCSBDate.builder()
        //                         .name("firstDayOfFestivus")
        //                         .quarterYYYYQ("20232")
        //                         .localDateTime(ldt2)
        //                         .build();

        //         String requestBody = mapper.writeValueAsString(ucsbDateEdited);

        //         when(ucsbDateRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbDateOrig));

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         put("/api/articles?id=67")
        //                                         .contentType(MediaType.APPLICATION_JSON)
        //                                         .characterEncoding("utf-8")
        //                                         .content(requestBody)
        //                                         .with(csrf()))
        //                         .andExpect(status().isOk()).andReturn();

        //         // assert
        //         verify(ucsbDateRepository, times(1)).findById(67L);
        //         verify(ucsbDateRepository, times(1)).save(ucsbDateEdited); // should be saved with correct user
        //         String responseString = response.getResponse().getContentAsString();
        //         assertEquals(requestBody, responseString);
        // }

        // @WithMockUser(roles = { "ADMIN", "USER" })
        // @Test
        // public void admin_cannot_edit_ucsbdate_that_does_not_exist() throws Exception {
        //         // arrange

        //         LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

        //         UCSBDate ucsbEditedDate = UCSBDate.builder()
        //                         .name("firstDayOfClasses")
        //                         .quarterYYYYQ("20222")
        //                         .localDateTime(ldt1)
        //                         .build();

        //         String requestBody = mapper.writeValueAsString(ucsbEditedDate);

        //         when(ucsbDateRepository.findById(eq(67L))).thenReturn(Optional.empty());

        //         // act
        //         MvcResult response = mockMvc.perform(
        //                         put("/api/articles?id=67")
        //                                         .contentType(MediaType.APPLICATION_JSON)
        //                                         .characterEncoding("utf-8")
        //                                         .content(requestBody)
        //                                         .with(csrf()))
        //                         .andExpect(status().isNotFound()).andReturn();

        //         // assert
        //         verify(ucsbDateRepository, times(1)).findById(67L);
        //         Map<String, Object> json = responseToJson(response);
        //         assertEquals("UCSBDate with id 67 not found", json.get("message"));

        // }
}
