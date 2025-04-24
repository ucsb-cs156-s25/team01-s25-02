package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

  @MockBean
  UCSBDiningCommonsMenuItemRepository repository;

  @MockBean
  UserRepository userRepository;

  @WithMockUser(roles = { "USER" })
  @Test
  public void test_logged_in_user_can_get_all_items() throws Exception {
    UCSBDiningCommonsMenuItem item1 = UCSBDiningCommonsMenuItem.builder()
        .diningCommonsCode("ortega")
        .name("Taco")
        .station("Main")
        .build();

    UCSBDiningCommonsMenuItem item2 = UCSBDiningCommonsMenuItem.builder()
        .diningCommonsCode("dlg")
        .name("Pasta")
        .station("Italian")
        .build();

    List<UCSBDiningCommonsMenuItem> expectedItems = Arrays.asList(item1, item2);
    when(repository.findAll()).thenReturn(expectedItems);

    MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
        .andExpect(status().isOk())
        .andReturn();

    verify(repository, times(1)).findAll();
    String expectedJson = mapper.writeValueAsString(expectedItems);
    String actualJson = response.getResponse().getContentAsString();
    assertEquals(expectedJson, actualJson);
  }

  @WithMockUser(roles = { "ADMIN", "USER" })
  @Test
  public void test_admin_can_post_item() throws Exception {
    UCSBDiningCommonsMenuItem itemToSave = UCSBDiningCommonsMenuItem.builder()
        .diningCommonsCode("ortega")
        .name("Taco")
        .station("Main")
        .build();

    UCSBDiningCommonsMenuItem savedItem = UCSBDiningCommonsMenuItem.builder()
        .id(1L)
        .diningCommonsCode("ortega")
        .name("Taco")
        .station("Main")
        .build();

    when(repository.save(eq(itemToSave))).thenReturn(savedItem);

    MvcResult response = mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post")
        .param("diningCommonsCode", "ortega")
        .param("name", "Taco")
        .param("station", "Main")
        .with(csrf()))
        .andExpect(status().isOk())
        .andReturn();

    verify(repository, times(1)).save(eq(itemToSave));
    String expectedJson = mapper.writeValueAsString(savedItem);
    String actualJson = response.getResponse().getContentAsString();
    assertEquals(expectedJson, actualJson);
  }
  @Test
  @WithMockUser(roles = { "USER" })
  public void test_getById_returns_item_when_exists() throws Exception {
        UCSBDiningCommonsMenuItem item = UCSBDiningCommonsMenuItem.builder()
            .diningCommonsCode("ortega")
            .name("Taco")
            .station("Main")
            .build();

        when(repository.findById(1L)).thenReturn(Optional.of(item));

        mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.diningCommonsCode").value("ortega"))
                .andExpect(jsonPath("$.name").value("Taco"))
                .andExpect(jsonPath("$.station").value("Main"));
}

  @Test
  @WithMockUser(roles = { "USER" })
  public void test_getById_returns_404_when_not_found() throws Exception {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=999"))
                .andExpect(status().isNotFound());
}
  @WithMockUser(roles = { "ADMIN", "USER" })
  @Test
  public void admin_can_edit_an_existing_item() throws Exception {
    // arrange
    UCSBDiningCommonsMenuItem originalItem = UCSBDiningCommonsMenuItem.builder()
            .diningCommonsCode("ortega")
            .name("Taco")
            .station("Main")
            .build();

    UCSBDiningCommonsMenuItem editedItem = UCSBDiningCommonsMenuItem.builder()
            .diningCommonsCode("portola")
            .name("Pasta")
            .station("Side")
            .build();

    String requestBody = mapper.writeValueAsString(editedItem);

    when(repository.findById(1L)).thenReturn(Optional.of(originalItem));

    // act
    MvcResult response = mockMvc.perform(
            put("/api/ucsbdiningcommonsmenuitem?id=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andReturn();

    // assert
    verify(repository, times(1)).findById(1L);
    verify(repository, times(1)).save(editedItem);
    assertEquals(requestBody, response.getResponse().getContentAsString());
}

  @WithMockUser(roles = { "ADMIN", "USER" })
  @Test
  public void admin_cannot_edit_item_that_does_not_exist() throws Exception {
    // arrange
    UCSBDiningCommonsMenuItem editedItem = UCSBDiningCommonsMenuItem.builder()
            .diningCommonsCode("ortega")
            .name("Taco")
            .station("Main")
            .build();

    String requestBody = mapper.writeValueAsString(editedItem);
    when(repository.findById(1L)).thenReturn(Optional.empty());

    // act
    MvcResult response = mockMvc.perform(
            put("/api/ucsbdiningcommonsmenuitem?id=1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("utf-8")
                    .content(requestBody)
                    .with(csrf()))
            .andExpect(status().isNotFound())
            .andReturn();

    // assert
    verify(repository, times(1)).findById(1L);
    String responseString = response.getResponse().getContentAsString();
    assertEquals("UCSBDiningCommonsMenuItem with id 1 not found", responseToJson(response).get("message"));
}

}
