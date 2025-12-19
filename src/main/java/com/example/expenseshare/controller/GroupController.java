package com.example.expenseshare.controller;

import com.example.expenseshare.model.Group;
import com.example.expenseshare.model.User;
import com.example.expenseshare.repository.GroupRepository;
import com.example.expenseshare.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupController(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        return groupRepository.save(group);
    }

    @PostMapping("/{groupId}/users")
    public Group addUsersToGroup(@PathVariable Long groupId,
                                 @RequestBody List<Long> userIds) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> users = userRepository.findAllById(userIds);
        group.setUsers(users);

        return groupRepository.save(group);
    }
}
