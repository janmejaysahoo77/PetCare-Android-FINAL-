package com.example.petcaresuperapp.presentation.screens.community

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcaresuperapp.domain.models.Comment
import com.example.petcaresuperapp.domain.models.SocialPost
import com.example.petcaresuperapp.domain.models.User
import com.example.petcaresuperapp.domain.repository.AuthRepository
import com.example.petcaresuperapp.domain.repository.SocialRepository
import com.example.petcaresuperapp.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val repository: SocialRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<SocialPost>>(emptyList())
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _createPostStatus = MutableStateFlow<PostStatus>(PostStatus.Idle)
    val createPostStatus: StateFlow<PostStatus> = _createPostStatus.asStateFlow()

    private val _likedPosts = MutableStateFlow<Set<String>>(emptySet())
    val likedPosts: StateFlow<Set<String>> = _likedPosts.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    init {
        loadCurrentUserId()
        loadPosts()
        loadUsers()
        loadCurrentUser()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            repository.currentUserIdFlow.collect { uid ->
                _currentUserId.value = uid
                if (uid != null) {
                    loadCurrentUser()
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            repository.getUsers().collect { userList ->
                _users.value = userList
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getUserData().collectLatest { result ->
                if (result is Resource.Success) {
                    _currentUser.value = result.data
                }
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getPosts().collect { postList ->
                _posts.value = postList
                _isLoading.value = false
                
                // Track which posts are liked by the current user
                postList.forEach { post ->
                    if (repository.isLiked(post.postId)) {
                        _likedPosts.value += post.postId
                    }
                }
            }
        }
    }

    fun createPost(imageUri: Uri, caption: String) {
        viewModelScope.launch {
            _createPostStatus.value = PostStatus.Loading
            val imageUrl = repository.uploadImage(imageUri)
            if (imageUrl != null) {
                repository.createPost(caption, imageUrl)
                _createPostStatus.value = PostStatus.Success
            } else {
                _createPostStatus.value = PostStatus.Error("Failed to upload image")
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
            if (_likedPosts.value.contains(postId)) {
                _likedPosts.value -= postId
            } else {
                _likedPosts.value += postId
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            repository.getComments(postId).collect { commentList ->
                _comments.value = commentList
            }
        }
    }

    fun addComment(postId: String, commentText: String) {
        viewModelScope.launch {
            repository.addComment(postId, commentText)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun resetPostStatus() {
        _createPostStatus.value = PostStatus.Idle
    }
}

sealed class PostStatus {
    object Idle : PostStatus()
    object Loading : PostStatus()
    object Success : PostStatus()
    data class Error(val message: String) : PostStatus()
}
