<template>
  <div class="create-post-page">
    <!-- 顶部导航 -->
    <div class="nav-bar">
      <button class="btn-back" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </button>
      <h1 class="nav-title">发布动态</h1>
      <button class="btn-publish" :class="{ active: canPublish }" @click="handlePublish" :disabled="!canPublish">
        发布
      </button>
    </div>

    <!-- 发布内容区 -->
    <div class="content-wrapper">
      <!-- 用户信息 -->
      <div class="user-info-bar">
        <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" class="user-avatar" />
        <div class="user-text">
          <span class="user-name">宠友123456</span>
          <span class="user-level">Lv.5 萌新达人</span>
        </div>
      </div>

      <!-- 分类选择 -->
      <div class="category-section">
        <div class="category-scroll">
          <button 
            v-for="cat in categories" 
            :key="cat" 
            :class="['category-chip', { active: selectedCategory === cat }]"
            @click="selectedCategory = cat"
          >
            {{ cat }}
          </button>
        </div>
      </div>

      <!-- 标题输入 -->
      <div class="input-section title-section">
        <textarea 
          v-model="form.title" 
          placeholder="给你的动态起个标题吧~" 
          class="title-input"
          rows="1"
          maxlength="50"
        ></textarea>
        <span class="char-count" :class="{ warning: form.title.length > 40 }">
          {{ form.title.length }}/50
        </span>
      </div>

      <!-- 内容输入 -->
      <div class="input-section content-section">
        <textarea 
          v-model="form.content" 
          placeholder="分享你的养宠心得、经验或有趣的故事..." 
          class="content-input"
          maxlength="2000"
        ></textarea>
        <span class="char-count" :class="{ warning: form.content.length > 1800 }">
          {{ form.content.length }}/2000
        </span>
      </div>

      <!-- 图片预览区 -->
      <div v-if="form.images.length > 0" class="images-preview">
        <div v-for="(img, index) in form.images" :key="index" class="preview-item">
          <img :src="img" alt="预览" />
          <button class="remove-btn" @click="removeImage(index)">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="tool-bar">
        <div class="tool-group">
          <button class="tool-btn" @click="triggerUpload" title="添加图片">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <rect x="3" y="3" width="18" height="18" rx="3" ry="3"/>
              <circle cx="8.5" cy="8.5" r="1.5"/>
              <polyline points="21 15 16 10 5 21"/>
            </svg>
            <span>图片</span>
          </button>
          <button class="tool-btn" title="@好友">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2"/>
              <circle cx="9" cy="7" r="4"/>
              <line x1="19" y1="8" x2="19" y2="14"/>
              <line x1="22" y1="11" x2="16" y2="11"/>
            </svg>
            <span>@好友</span>
          </button>
          <button class="tool-btn" title="添加话题">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <line x1="4" y1="9" x2="20" y2="9"/>
              <line x1="4" y1="15" x2="20" y2="15"/>
              <line x1="10" y1="3" x2="8" y2="21"/>
              <line x1="16" y1="3" x2="14" y2="21"/>
            </svg>
            <span>话题</span>
          </button>
          <button class="tool-btn" title="添加位置">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
              <circle cx="12" cy="10" r="3"/>
            </svg>
            <span>位置</span>
          </button>
        </div>
        
        <div class="image-count" v-if="form.images.length > 0">
          <span>{{ form.images.length }}/9</span>
        </div>
      </div>

      <!-- 话题标签 -->
      <div class="tags-section">
        <div class="tags-label">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/>
            <line x1="7" y1="7" x2="7.01" y2="7"/>
          </svg>
          <span>添加话题标签</span>
        </div>
        <div class="tags-container">
          <div class="tags-list" v-if="form.tags.length > 0">
            <span v-for="(tag, index) in form.tags" :key="index" class="tag-chip">
              #{{ tag }}
              <button @click="removeTag(index)">×</button>
            </span>
          </div>
          <input 
            v-if="form.tags.length < 5" 
            type="text" 
            v-model="tagInput" 
            placeholder="输入话题后按回车添加"
            @keydown.enter.prevent="addTag"
            class="tag-input"
          />
        </div>
      </div>

      <!-- 推荐话题 -->
      <div class="recommend-section">
        <div class="section-header">
          <span class="section-title">推荐话题</span>
        </div>
        <div class="recommend-tags">
          <button 
            v-for="tag in recommendTags" 
            :key="tag" 
            class="recommend-tag"
            @click="form.tags.push(tag), form.tags = form.tags.slice(-5)"
          >
            #{{ tag }}
          </button>
        </div>
      </div>
    </div>

    <!-- 隐藏的文件输入 -->
    <input type="file" ref="fileInput" accept="image/*" multiple @change="handleFileChange" style="display: none" />

    <!-- Toast 提示 -->
    <Transition name="toast">
      <div v-if="toast.show" class="toast" :class="toast.type">
        <svg v-if="toast.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="20 6 9 17 4 12"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="12"/>
          <line x1="12" y1="16" x2="12.01" y2="16"/>
        </svg>
        {{ toast.message }}
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const categories = ["晒宠", "问答", "种草", "日常", "知识", "视频", "好物"];
const selectedCategory = ref("日常");
const tagInput = ref("");
const fileInput = ref<HTMLInputElement | null>(null);

const form = reactive({
  title: "",
  content: "",
  images: [] as string[],
  tags: [] as string[]
});

const recommendTags = ["新手养猫", "狗狗训练", "自制零食", "宠物摄影", "春季驱虫", "养兔指南", "布偶猫", "宠物健康"];

const toast = reactive({
  show: false,
  message: "",
  type: "success" as "success" | "error"
});

const canPublish = computed(() => {
  return form.title.trim().length > 0 && form.content.trim().length > 0;
});

const goBack = () => {
  router.back();
};

const triggerUpload = () => {
  fileInput.value?.click();
};

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement;
  const files = target.files;
  if (!files) return;

  Array.from(files).forEach(file => {
    if (form.images.length >= 9) return;
    const reader = new FileReader();
    reader.onload = (event) => {
      if (event.target?.result) {
        form.images.push(event.target.result as string);
      }
    };
    reader.readAsDataURL(file);
  });
};

const removeImage = (index: number) => {
  form.images.splice(index, 1);
};

const addTag = () => {
  const tag = tagInput.value.trim();
  if (tag && !form.tags.includes(tag) && form.tags.length < 5) {
    form.tags.push(tag);
    tagInput.value = "";
  }
};

const removeTag = (index: number) => {
  form.tags.splice(index, 1);
};

const showToast = (message: string, type: "success" | "error" = "success") => {
  toast.message = message;
  toast.type = type;
  toast.show = true;
  setTimeout(() => {
    toast.show = false;
  }, 2000);
};

const handlePublish = () => {
  if (!canPublish.value) return;
  
  console.log("发布内容:", {
    category: selectedCategory.value,
    ...form
  });

  showToast("发布成功！", "success");
  
  setTimeout(() => {
    router.push("/community");
  }, 1500);
};
</script>

<style scoped lang="scss">
.create-post-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fdfb 0%, #fff 100%);
  display: flex;
  flex-direction: column;
  padding-bottom: 20px;
}

// 顶部导航
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border-bottom: 1px solid rgba(255, 155, 122, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
}

.btn-back {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(255, 155, 122, 0.1) 0%, rgba(91, 185, 140, 0.1) 100%);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;

  svg {
    width: 22px;
    height: 22px;
    color: var(--primary);
  }

  &:hover {
    background: linear-gradient(135deg, rgba(255, 155, 122, 0.2) 0%, rgba(91, 185, 140, 0.2) 100%);
    transform: scale(1.05);
  }
}

.nav-title {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-heading);
  margin: 0;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.btn-publish {
  padding: 8px 20px;
  background: #e0e0e0;
  color: #999;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &.active {
    background: var(--hero-gradient);
    color: #fff;
    box-shadow: 0 4px 16px rgba(255, 155, 122, 0.4);
  }

  &:hover.active {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(255, 155, 122, 0.5);
  }
}

// 内容区域
.content-wrapper {
  flex: 1;
  padding: 20px 16px;
  max-width: 720px;
  margin: 0 auto;
  width: 100%;
}

// 用户信息栏
.user-info-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(34, 60, 52, 0.06);

  .user-avatar {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    border: 3px solid rgba(255, 155, 122, 0.3);
  }

  .user-text {
    display: flex;
    flex-direction: column;
    gap: 2px;

    .user-name {
      font-size: 16px;
      font-weight: 700;
      color: var(--text-heading);
    }

    .user-level {
      font-size: 12px;
      color: var(--primary);
      font-weight: 600;
    }
  }
}

// 分类选择
.category-section {
  margin-bottom: 16px;
}

.category-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 4px 0;
  
  &::-webkit-scrollbar {
    display: none;
  }
}

.category-chip {
  flex-shrink: 0;
  padding: 8px 18px;
  background: #fff;
  border: 1.5px solid rgba(255, 155, 122, 0.3);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    border-color: var(--primary);
    color: var(--primary);
  }

  &.active {
    background: var(--hero-gradient);
    border-color: transparent;
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  }
}

// 输入区域
.input-section {
  position: relative;
  margin-bottom: 16px;
}

.title-input {
  width: 100%;
  padding: 14px 16px;
  background: #fff;
  border: 1.5px solid rgba(255, 155, 122, 0.2);
  border-radius: 12px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  outline: none;
  resize: none;
  transition: all 0.3s ease;
  line-height: 1.4;

  &::placeholder {
    color: rgba(34, 60, 52, 0.3);
    font-weight: 500;
  }

  &:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(255, 155, 122, 0.1);
  }
}

.content-input {
  width: 100%;
  min-height: 160px;
  padding: 16px;
  background: #fff;
  border: 1.5px solid rgba(255, 155, 122, 0.2);
  border-radius: 12px;
  font-size: 15px;
  color: var(--text);
  line-height: 1.7;
  outline: none;
  resize: vertical;
  transition: all 0.3s ease;

  &::placeholder {
    color: rgba(34, 60, 52, 0.3);
  }

  &:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(255, 155, 122, 0.1);
  }
}

.char-count {
  position: absolute;
  right: 12px;
  bottom: -22px;
  font-size: 12px;
  color: var(--muted);
  
  &.warning {
    color: #ff6b6b;
  }
}

// 图片预览
.images-preview {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 20px 0;

  .preview-item {
    position: relative;
    aspect-ratio: 1;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(34, 60, 52, 0.1);

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .remove-btn {
      position: absolute;
      top: 6px;
      right: 6px;
      width: 26px;
      height: 26px;
      background: rgba(0, 0, 0, 0.5);
      border: none;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s ease;

      svg {
        width: 14px;
        height: 14px;
        color: #fff;
      }

      &:hover {
        background: rgba(255, 107, 107, 0.8);
        transform: scale(1.1);
      }
    }
  }
}

// 工具栏
.tool-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-top: 1px solid rgba(255, 155, 122, 0.15);
  border-bottom: 1px solid rgba(255, 155, 122, 0.15);
  margin-bottom: 20px;
}

.tool-group {
  display: flex;
  gap: 4px;
}

.tool-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: transparent;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 22px;
    height: 22px;
  }

  span {
    font-weight: 500;
  }

  &:hover {
    background: rgba(255, 155, 122, 0.1);
    color: var(--primary);
  }
}

.image-count {
  font-size: 13px;
  color: var(--primary);
  font-weight: 600;
}

// 话题标签
.tags-section {
  margin-bottom: 20px;
}

.tags-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--muted);
  margin-bottom: 10px;

  svg {
    width: 18px;
    height: 18px;
  }
}

.tags-container {
  background: #fff;
  border: 1.5px solid rgba(255, 155, 122, 0.2);
  border-radius: 12px;
  padding: 12px;
  transition: all 0.3s ease;

  &:focus-within {
    border-color: var(--primary);
    box-shadow: 0 0 0 4px rgba(255, 155, 122, 0.1);
  }
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: linear-gradient(135deg, rgba(255, 155, 122, 0.15) 0%, rgba(91, 185, 140, 0.15) 100%);
  color: var(--primary);
  border-radius: 16px;
  font-size: 13px;
  font-weight: 600;

  button {
    background: none;
    border: none;
    color: var(--primary);
    font-size: 16px;
    cursor: pointer;
    padding: 0;
    line-height: 1;
    margin-left: 2px;
  }
}

.tag-input {
  width: 100%;
  padding: 6px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--text);

  &::placeholder {
    color: rgba(34, 60, 52, 0.4);
  }
}

// 推荐话题
.recommend-section {
  background: linear-gradient(135deg, rgba(255, 155, 122, 0.05) 0%, rgba(91, 185, 140, 0.05) 100%);
  border-radius: 16px;
  padding: 16px;
}

.section-header {
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.recommend-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.recommend-tag {
  padding: 6px 14px;
  background: #fff;
  border: 1px solid rgba(255, 155, 122, 0.3);
  border-radius: 16px;
  font-size: 12px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: var(--hero-gradient);
    border-color: transparent;
    color: #fff;
  }
}

// Toast
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--hero-gradient);
  color: #fff;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(255, 155, 122, 0.4);

  svg {
    width: 18px;
    height: 18px;
  }

  &.error {
    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
    box-shadow: 0 4px 20px rgba(255, 107, 107, 0.4);
  }
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-20px);
}

// 响应式
@media (max-width: 480px) {
  .tool-btn span {
    display: none;
  }

  .images-preview {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
