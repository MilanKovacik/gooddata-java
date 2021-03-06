/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.GoodDataSettings;
import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.md.Meta;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    private static final String ACCOUNT_ID = "17";
    private static final String ID = "11";
    private static final String URI = "/gdc/projects/11";
    private static final String ROLE_URI = URI + "/roles/2";
    private static final String ROLE_TITLE = "role_title";

    @Mock
    private Project project;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AccountService accountService;
    @Mock
    private Account account;

    private ProjectService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ProjectService(restTemplate, accountService, new GoodDataSettings());
        when(accountService.getCurrent()).thenReturn(account);
        when(account.getId()).thenReturn(ACCOUNT_ID);
        when(project.getId()).thenReturn(ID);
    }

    @Test
    public void testGetProjects() {
        when(restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, ACCOUNT_ID))
                .thenReturn(new Projects(singletonList(project)));
        final Collection<Project> result = service.getProjects();

        assertThat(result, hasSize(1));
        assertThat(result, hasItem(project));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectsWithClientException() {
        when(restTemplate.getForObject(Project.PROJECTS_URI, Projects.class, ACCOUNT_ID))
                .thenThrow(new GoodDataException(""));
        service.getProjects();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullUri() {
        service.getProjectByUri(null);
    }

    @Test
    public void testGetProjectByUri() {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectByUri(URI);
        assertThat(result, is(project));
    }

    @Test(expectedExceptions = ProjectNotFoundException.class)
    public void testGetProjectByUriNotFound() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(404, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetProjectByUriServerError() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetProjectByUriClientError() {
        when(restTemplate.getForObject(URI, Project.class)).thenThrow(new RestClientException(""));
        service.getProjectByUri(URI);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetProjectByUriWithNullId() {
        service.getProjectById(null);
    }

    @Test
    public void testGetProjectById() {
        when(restTemplate.getForObject(URI, Project.class)).thenReturn(project);

        final Project result = service.getProjectById(ID);
        assertThat(result, is(project));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullProject() {
        service.addUserToProject(null, mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullAccount() {
        service.addUserToProject(mock(Project.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddUserToProjectNullAccountUri() {
        service.addUserToProject(mock(Project.class), mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateUserInProjectNullProject() {
        service.updateUserInProject(null, mock(User.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateUserInProjectNullUser() {
        service.updateUserInProject(mock(Project.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetUserInProjectNullProject() {
        service.getUser(null, mock(Account.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetUserInProjectNullAccount() {
        service.getUser(mock(Project.class), null);
    }

    @Test
    public void testGetRoleByUri() {
        final Role role = new Role(null, new Meta(ROLE_TITLE), null);
        when(restTemplate.getForObject(ROLE_URI, Role.class)).thenReturn(role);

        final Role roleByUri = service.getRoleByUri(ROLE_URI);

        assertThat(roleByUri.getUri(), is(ROLE_URI));
        assertThat(roleByUri.getTitle(), is(ROLE_TITLE));
    }
}