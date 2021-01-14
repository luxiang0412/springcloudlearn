package com.luxiang.swagger3.restdocopenapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * @author luxiang
 * description  //TODO
 * create       2020-07-01 16:27
 */
@RestController
@RequestMapping("/foo")
public class FooController {

    @Autowired
    FooRepository repository;

    @GetMapping
    public ResponseEntity<List<Foo>> getAllFoos() {
        List<Foo> fooList = (List<Foo>) repository.findAll();
        if (fooList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fooList, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Foo> getFooById(@PathVariable("id") Long id) {

        Optional<Foo> foo = repository.findById(id);
        return foo.isPresent() ? new ResponseEntity<>(foo.get(), HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Foo> addFoo(@RequestBody @Valid Foo foo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(FooController.class).slash(foo.getId())
                .toUri());
        Foo savedFoo;
        try {
            savedFoo = repository.save(foo);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(savedFoo, httpHeaders, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoo(@PathVariable("id") long id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Foo> updateFoo(@PathVariable("id") long id, @RequestBody Foo foo) {
        boolean isFooPresent = repository.existsById(Long.valueOf(id));

        if (!isFooPresent) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Foo updatedFoo = repository.save(foo);

        return new ResponseEntity<>(updatedFoo, HttpStatus.OK);
    }
}