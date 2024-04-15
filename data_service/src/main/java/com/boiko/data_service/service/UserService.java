package com.boiko.data_service.service;

import com.boiko.data_service.dto.BecomeAuthorRequest;
import com.boiko.data_service.dto.UserDTO;
import com.boiko.data_service.mapper.AuthorLinkMapper;
import com.boiko.data_service.model.Author;
import com.boiko.data_service.model.AuthorLink;
import com.boiko.data_service.model.User;
import com.boiko.data_service.repository.AuthorLinkRepository;
import com.boiko.data_service.repository.AuthorRepository;
import com.boiko.data_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final AuthorLinkRepository linkRepository;
    private final FileInfoService fileInfoService;
    private final AuthorLinkMapper linkMapper;

    public User addUser(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.email())
                .password(userDTO.password())
                .nickname(userDTO.nickname())
                .birthday(userDTO.birthday())
                .avatar(fileInfoService.getDefaultAvatar())
                .dateRegistered(LocalDate.now())
                .timeRegistered(LocalTime.now())
                .build();
        return userRepository.save(user);
    }

    public User findByID(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id = %d doesn't exist".formatted(id)));
    }

    public Author becomeAuthor(Long id, BecomeAuthorRequest request) {
        User user = findByID(id);
        List<AuthorLink> links = new ArrayList<>(request.links()
                .stream()
                .map(linkMapper::dtoToModel)
                .peek(x -> x.setAuthorID(id))
                .toList());

        Author author = Author.builder()
                .id(id)
                .biography(request.biography())
                .build();
        author = authorRepository.save(author);
        linkRepository.saveAll(links);
        author.setUserdata(user);
        author.setLinks(links);
        return authorRepository.save(author);
    }
}
