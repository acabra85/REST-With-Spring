package com.baeldung.um.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.baeldung.common.util.QueryConstants;
import com.baeldung.common.web.controller.AbstractController;
import com.baeldung.common.web.controller.ISortingController;
import com.baeldung.um.persistence.model.User;
import com.baeldung.um.service.IUserService;
import com.baeldung.um.util.Um.Privileges;
import com.baeldung.um.util.UmMappings;

@Controller
@RequestMapping(value = UmMappings.USERS)
public class UserController extends AbstractController<User> implements ISortingController<User> {

    @Autowired
    private IUserService service;

    // API

    // find - all/paginated

    @Override
    @RequestMapping(params = { QueryConstants.PAGE, QueryConstants.SIZE, QueryConstants.SORT_BY }, method = RequestMethod.GET)
    @ResponseBody
    @Secured(Privileges.CAN_USER_READ)
    public List<User> findAllPaginatedAndSorted(@RequestParam(value = QueryConstants.PAGE) final int page, @RequestParam(value = QueryConstants.SIZE) final int size, @RequestParam(value = QueryConstants.SORT_BY) final String sortBy,
        @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return findPaginatedAndSortedInternal(page, size, sortBy, sortOrder);
    }

    @Override
    @RequestMapping(params = { QueryConstants.PAGE, QueryConstants.SIZE }, method = RequestMethod.GET)
    @ResponseBody
    @Secured(Privileges.CAN_USER_READ)
    public List<User> findAllPaginated(@RequestParam(value = QueryConstants.PAGE) final int page, @RequestParam(value = QueryConstants.SIZE) final int size) {
        return findPaginatedInternal(page, size);
    }

    @Override
    @RequestMapping(params = { QueryConstants.SORT_BY }, method = RequestMethod.GET)
    @ResponseBody
    @Secured(Privileges.CAN_USER_READ)
    public List<User> findAllSorted(@RequestParam(value = QueryConstants.SORT_BY) final String sortBy, @RequestParam(value = QueryConstants.SORT_ORDER) final String sortOrder) {
        return findAllSortedInternal(sortBy, sortOrder);
    }

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Secured(Privileges.CAN_USER_READ)
    public List<User> findAll(final HttpServletRequest request) {
        return findAllInternal(request);
    }

    // find - one

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Secured(Privileges.CAN_USER_READ)
    public User findOne(@PathVariable("id") final Long id) {
        return findOneInternal(id);
    }

    // create

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(Privileges.CAN_PRIVILEGE_CREATE)
    public void create(@RequestBody @Valid final User resource) {
        createInternal(resource);
    }

    // update

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Secured(Privileges.CAN_USER_UPDATE)
    public void update(@PathVariable("id") final Long id, @RequestBody @Valid final User resource) {
        updateInternal(id, resource);
    }

    // delete

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(Privileges.CAN_USER_DELETE)
    public void delete(@PathVariable("id") final Long id) {
        deleteByIdInternal(id);
    }

    // Spring

    @Override
    protected final IUserService getService() {
        return service;
    }

}
