package com.boiko.data_service.service;

import com.boiko.data_service.dto.BecomeAuthorRequest;
import com.boiko.data_service.dto.UserDTO;
import com.boiko.data_service.dto.UserDetailsDTO;
import com.boiko.data_service.mapper.AuthorLinkMapper;
import com.boiko.data_service.mapper.UserMapper;
import com.boiko.data_service.model.*;
import com.boiko.data_service.repository.AuthorLinkRepository;
import com.boiko.data_service.repository.AuthorRepository;
import com.boiko.data_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final AuthorLinkRepository linkRepository;
    private final FileInfoService fileInfoService;
    private final SongService songService;
    private final AlbumService albumService;
    private final AuthorLinkMapper linkMapper;
    private final UserMapper userMapper;

    public User saveUser(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.email())
                .password(userDTO.password())
                .nickname(userDTO.nickname())
                .birthday(userDTO.birthday())
                .avatar(fileInfoService.getDefaultAvatar())
                .timestampRegistered(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return userRepository.save(user);
    }

    public UserDetailsDTO findUserDTOByID(Long id) {
        return userMapper.modelToDTO(
                userRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("User with id = %d doesn't exist".formatted(id)))
        );
    }

    private User findUserByID(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id = %d doesn't exist".formatted(id)));
    }

    public Author findAuthorByID(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author with id = %d doesn't exist".formatted(id)));
    }

    public Author becomeAuthor(BecomeAuthorRequest request) {
        User user = findUserByID(request.userID());
        List<AuthorLink> links = new ArrayList<>(request.links()
                .stream()
                .map(linkMapper::dtoToModel)
                .peek(x -> x.setAuthorID(request.userID()))
                .toList());

        Author author = Author.builder()
                .id(request.userID())
                .biography(request.biography())
                .build();
        author = authorRepository.save(author);
        linkRepository.saveAll(links);
        author.setUserdata(user);
        author.setLinks(links);
        return authorRepository.save(author);
    }

    public Long signUp(UserDTO userDTO) {
        boolean isExists = userRepository.existsByEmail(userDTO.email());
        if (isExists) {
            throw new RuntimeException("User with email = %s already exists".formatted(userDTO.email()));
        }
        return saveUser(userDTO).getId();
    }

    public Long logIn(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(email, password);
        if (optionalUser.isEmpty()) {
                throw new RuntimeException("Invalid email or password");
        }
        return optionalUser.get().getId();
    }

    private <T> Long toggleLike(User user, T entity, long totalLikes, List<T> entities) {
        if (entities.contains(entity)) {
            entities.remove(entity);
            totalLikes--;
        }
        else {
            entities.add(entity);
            totalLikes++;
        }
        userRepository.save(user);
        return totalLikes;
    }

    public Long toggleLikeSong(Long userID, Long songID) {
        User user = findUserByID(userID);
        Song song = songService.findByID(songID);
        long likes = song.getLikes();
        return toggleLike(user, song, likes, user.getLikedSongs());
    }

    public Long toggleLikeAlbum(Long userID, Long albumID) {
        User user = findUserByID(userID);
        Album album = albumService.findByID(albumID);
        long likes = album.getLikes();
        return toggleLike(user, album, likes, user.getLikedAlbums());
    }

    public Long toggleLikeAuthor(Long userID, Long authorID) {
        User user = findUserByID(userID);
        Author author = findAuthorByID(authorID);
        long likes = author.getLikes();
        return toggleLike(user, author, likes, user.getLikedAuthors());
    }
}
