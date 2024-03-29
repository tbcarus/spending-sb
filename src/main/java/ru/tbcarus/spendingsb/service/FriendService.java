package ru.tbcarus.spendingsb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.repository.JpaFriendRepository;

@Service
public class FriendService {

    @Autowired
    JpaFriendRepository friendRepository;

}
