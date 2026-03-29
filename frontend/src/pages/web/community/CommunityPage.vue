<template>
  <section class="card page">
    <div class="top-row">
      <h2 class="section-title">社区交流</h2>
      <select v-model="tab" class="input" @change="loadPosts">
        <option value="recommended">推荐</option>
        <option value="latest">最新</option>
      </select>
    </div>

    <form class="create-box" @submit.prevent="submitPost">
      <input v-model.trim="newPost.title" class="input" placeholder="标题" />
      <textarea v-model.trim="newPost.content" class="input textarea" placeholder="写下你的养宠经验..." />
      <div class="create-actions">
        <input v-model.trim="newPost.category" class="input" placeholder="分类，如 knowledge" />
        <button class="btn btn-primary" :disabled="posting">{{ posting ? "发布中..." : "发布帖子" }}</button>
      </div>
    </form>

    <DataState :loading="loading" :error="error" :empty="posts.length === 0" empty-text="暂时还没有帖子">
      <div class="post-list">
        <article v-for="post in posts" :key="post.id" class="post-card">
          <div class="post-main" @click="openDetail(post.id)">
            <h3>{{ post.title }}</h3>
            <p class="muted">{{ post.excerpt || "点击查看详情" }}</p>
            <div class="meta muted">点赞 {{ post.like_count }} · 评论 {{ post.comment_count }}</div>
          </div>
          <div class="post-ops"><button class="btn btn-secondary" @click="like(post.id)">点赞</button></div>
        </article>
      </div>
    </DataState>

    <section v-if="detail" class="detail card inner-card">
      <h3>{{ detail.title }}</h3>
      <p class="muted">{{ detail.content }}</p>
      <div class="comment-box">
        <input v-model.trim="commentText" class="input" placeholder="写评论..." />
        <button class="btn btn-secondary" @click="submitComment" :disabled="commenting">评论</button>
      </div>
      <ul class="comments">
        <li v-for="comment in comments" :key="comment.id"><strong>{{ comment.author?.nickname || "用户" }}</strong>：{{ comment.content }}</li>
      </ul>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { createComment, createPost, fetchPostComments, fetchPostDetail, fetchPosts, toggleLike } from "@/services/modules/community";
import { toErrorMessage } from "@/services/http";
import type { PostComment, PostDetail, PostSummary } from "@/types/community";

const loading = ref(false);
const posting = ref(false);
const commenting = ref(false);
const error = ref("");
const tab = ref("recommended");
const posts = ref<PostSummary[]>([]);
const detail = ref<PostDetail | null>(null);
const comments = ref<PostComment[]>([]);
const commentText = ref("");

const newPost = reactive({ title: "", content: "", category: "knowledge" });

const loadPosts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchPosts({ tab: tab.value, page: 1, page_size: 10 });
    posts.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const openDetail = async (postId: number) => {
  try {
    detail.value = await fetchPostDetail(postId);
    const commentData = await fetchPostComments(postId);
    comments.value = commentData.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const submitPost = async () => {
  posting.value = true;
  try {
    await createPost({ ...newPost });
    newPost.title = "";
    newPost.content = "";
    await loadPosts();
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    posting.value = false;
  }
};

const submitComment = async () => {
  if (!detail.value || !commentText.value) return;
  commenting.value = true;
  try {
    await createComment(detail.value.id, commentText.value);
    commentText.value = "";
    const commentData = await fetchPostComments(detail.value.id);
    comments.value = commentData.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    commenting.value = false;
  }
};

const like = async (postId: number) => {
  try {
    await toggleLike(postId);
    await loadPosts();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

onMounted(loadPosts);
</script>

<style scoped lang="scss">
.page { display: grid; gap: 14px; }
.top-row { display: flex; justify-content: space-between; gap: 12px; }
.create-box { display: grid; gap: 8px; background: #f9fcfb; border: 1px solid #e4eeea; border-radius: 12px; padding: 12px; }
.create-actions { display: grid; grid-template-columns: 1fr auto; gap: 8px; }
.textarea { min-height: 80px; }
.post-list { display: grid; gap: 10px; }
.post-card { border: 1px solid #e5efeb; border-radius: 12px; padding: 12px; display: grid; grid-template-columns: 1fr auto; gap: 10px; }
.post-main { cursor: pointer; }
.post-main h3 { margin: 0; }
.meta { margin-top: 8px; font-size: 13px; }
.inner-card { border: 1px dashed #d4e6df; }
.comment-box { display: grid; grid-template-columns: 1fr auto; gap: 8px; margin: 8px 0; }
.comments { margin: 0; padding-left: 18px; }
</style>
