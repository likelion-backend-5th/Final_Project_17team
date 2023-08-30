package com.example.final_project_17team.post.service;

import com.example.final_project_17team.comment.dto.CommentDto;
import com.example.final_project_17team.comment.entity.Comment;
import com.example.final_project_17team.comment.repository.CommentRepository;
import com.example.final_project_17team.post.dto.PostDto;
import com.example.final_project_17team.post.entity.Post;
import com.example.final_project_17team.post.repository.PostRepository;
import com.example.final_project_17team.restaurant.repository.RestaurantRepository;
import com.example.final_project_17team.user.entity.User;
import com.example.final_project_17team.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 동행 음식점 미지정인 경우 : restaurantId = 0
    public PostDto createPost(PostDto dto, Long restaurantId){
        User user = loadUserByAuth();
        if (restaurantRepository.findById(restaurantId).isEmpty() && restaurantId != 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setStatus("모집 중");
        post.setVisitDate(dto.getVisitDate());
        post.setPrefer(dto.getPrefer());
        post.setUserId(user.getId());
        if (restaurantId != 0) post.setRestaurantId(restaurantId);

        postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    // [제목, 내용, 방문날짜, 선호, 모집현황]만 수정 가능
    public PostDto updatePost(PostDto dto, Long postId){
        User user = loadUserByAuth();
        Post post = loadPostById(postId, user.getId());

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setPrefer(dto.getPrefer());
        post.setVisitDate(dto.getVisitDate());

        if (dto.getStatus().equals("모집 중") || dto.getStatus().equals("모집 완료"))
            post.setStatus(dto.getStatus());

        postRepository.save(post);
        return PostDto.fromEntity(post);
    }

    public void deletePost(Long postId){
        User user = loadUserByAuth();
        Post post = loadPostById(postId, user.getId());

        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // [검색어, 성별, 나이, 모집현황]으로 검색 가능
    public Page<PostDto> searchPost(String targets, Integer pageNumber, Integer pageSize, String gender, Integer age, String status) {
        List<String> targetList = Arrays.asList(targets.split(" "));
        List<Post> postList = postRepository.findAll();
        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : postList){
            if (targets.equals("")) break;
            int cnt = 0;
            for (String target : targetList) {
                if (post.getTitle().contains(target) || post.getContent().contains(target)) {
                    if (++cnt == targetList.size()) postDtoList.add(PostDto.fromEntity(post));
                } else break;
            }
        }

        postDtoList = searchByGenderAgeStatus(postDtoList, gender, age, status);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), postDtoList.size());
        return new PageImpl<>(postDtoList.subList(start, end), pageRequest, postDtoList.size());
    }

    public CommentDto crateComment(CommentDto dto, Long postId) {
        User user = loadUserByAuth();
        Post post = loadPostById(postId, user.getId());

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setUserId(user.getId());
        comment.setPostId(post.getId());

        commentRepository.save(comment);
        return CommentDto.fromEntity(comment);
    }

    // TODO 동행 조회(전체 조회와 제목, 내용에 키워드 포함 조회는 구현 했는데 gender, age, status 등으로 조회하는건 아직 구현 안함)
    // TODO 댓글 조회(동행 글 작성자와, 댓글 작성자 들만 볼 수 있게), 수정, 삭제

    public List<PostDto> searchByGenderAgeStatus(List<PostDto> list, String genderStr, Integer age, String status) {
        Boolean gender = genderStr.equals("male")? true : false;
        List<PostDto> responseList = new ArrayList<>();

        for (PostDto post : list) {
            User user = loadUserById(post.getUserId());
            if (!genderStr.equals("") && (user.isGender() != gender)) continue;
            if (!age.equals("") && (user.getAge()/10 != age || age != 0)) continue;
            if (!status.equals("") && (post.getStatus().equals(status))) continue;
            responseList.add(post);
        }
        return responseList;
    }

    public User loadUserByAuth() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        return optionalUser.get();
    }

    public User loadUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        return optionalUser.get();
    }

    public Post loadPostById(Long postId, Long userId) {
        Optional<Post> optionalPost = postRepository.findByIdAndUserId(postId, userId);
        if(optionalPost.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "post not found");
        return optionalPost.get();
    }
}
