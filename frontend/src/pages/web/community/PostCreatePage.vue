<template>
  <div class="create-post-page">
    <!-- 顶部导航 -->
    <header class="nav-header">
      <div class="nav-left">
        <button class="back-btn" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>
      </div>
      <h1 class="nav-title">发布笔记</h1>
      <button class="publish-btn" :class="{ active: canPublish }" @click="handlePublish" :disabled="!canPublish">
        发布
      </button>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="user-avatar-wrapper">
          <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Felix" class="user-avatar" />
          <div class="user-level-badge">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            Lv.5
          </div>
        </div>
        <div class="user-info">
          <span class="user-name">宠友123456</span>
          <span class="user-desc">分享养宠日常，记录美好生活</span>
        </div>
      </div>

      <!-- 分类选择器 -->
      <div class="category-selector">
        <div class="selector-label">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
            <polyline points="22,6 12,13 2,6"/>
          </svg>
          选择分类
        </div>
        <div class="category-tabs">
          <button 
            v-for="cat in categories" 
            :key="cat.value" 
            :class="['category-tab', { active: selectedCategory === cat.value }]"
            :style="selectedCategory === cat.value ? { background: cat.color, borderColor: cat.color } : {}"
            @click="selectedCategory = cat.value"
          >
            <span class="tab-icon">{{ cat.label.charAt(0) }}</span>
            {{ cat.label }}
          </button>
        </div>
      </div>

      <!-- 标题输入 -->
      <div class="title-input-wrapper">
        <textarea 
          v-model="form.title" 
          placeholder="添加标题，让更多人看到~" 
          class="title-textarea"
          rows="1"
          maxlength="50"
          ref="titleTextareaRef"
          @input="autoResizeTitle"
        ></textarea>
        <span class="title-count" :class="{ warning: form.title.length > 40 }">
          {{ form.title.length }}/50
        </span>
      </div>

      <!-- 内容输入 -->
      <div class="content-input-wrapper">
        <textarea 
          v-model="form.content" 
          placeholder="分享你的养宠心得、经验或有趣的故事..." 
          class="content-textarea"
          maxlength="2000"
        ></textarea>
        <div class="content-footer">
          <span class="content-count" :class="{ warning: form.content.length > 1800 }">
            {{ form.content.length }}/2000
          </span>
        </div>
      </div>

      <!-- 图片上传区域 -->
      <div class="image-uploader">
        <div v-if="form.images.length > 0" class="image-grid">
          <div 
            v-for="(img, index) in form.images" 
            :key="index" 
            class="image-item"
            :class="{ 'first-image': index === 0 && form.images.length > 1 }"
          >
            <img :src="img" alt="预览" />
            <button class="remove-btn" @click="removeImage(index)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
            <div v-if="index === 0" class="cover-tag">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <polyline points="21 15 16 10 5 21"/>
              </svg>
              封面
            </div>
          </div>
        </div>
        <div class="upload-area" @click="triggerUpload" v-if="form.images.length < 9">
          <div class="upload-content">
            <div class="upload-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <rect x="3" y="3" width="18" height="18" rx="3" ry="3"/>
                <line x1="12" y1="8" x2="12" y2="16"/>
                <line x1="8" y1="12" x2="16" y2="12"/>
              </svg>
            </div>
            <span class="upload-text">{{ form.images.length > 0 ? '继续添加' : '添加图片' }}</span>
            <span class="upload-hint">最多9张，支持jpg、png</span>
          </div>
        </div>
      </div>

      <!-- 标签区域 -->
      <div class="tags-section">
        <div class="section-header">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/>
            <line x1="7" y1="7" x2="7.01" y2="7"/>
          </svg>
          <span>添加标签</span>
          <span class="tag-tip">最多5个</span>
        </div>
        <div class="tags-input-area">
          <div class="selected-tags" v-if="form.tags.length > 0">
            <span v-for="(tag, index) in form.tags" :key="index" class="selected-tag">
              #{{ tag }}
              <button @click="removeTag(index)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                  <path d="M18 6L6 18M6 6l12 12"/>
                </svg>
              </button>
            </span>
          </div>
          <div class="tag-input-wrapper">
            <input 
              v-if="form.tags.length < 5" 
              type="text" 
              v-model="tagInput" 
              placeholder="输入话题后按回车"
              @keydown.enter.prevent="addTag"
              class="tag-input"
            />
            <button v-if="tagInput" class="add-tag-btn" @click="addTag">添加</button>
          </div>
        </div>
        
        <!-- 推荐话题 -->
        <div class="recommend-tags">
          <span class="recommend-label">推荐：</span>
          <div class="tags-scroll">
            <button 
              v-for="tag in recommendTags" 
              :key="tag"
              :class="['recommend-tag', { selected: form.tags.includes(tag) }]"
              @click="toggleRecommendTag(tag)"
            >
              #{{ tag }}
            </button>
          </div>
        </div>
      </div>

      <!-- 附加功能 -->
      <div class="extra-options">
        <button class="option-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M16 21v-2a4 4 0 00-4-4H6a4 4 0 00-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <line x1="19" y1="8" x2="19" y2="14"/>
            <line x1="22" y1="11" x2="16" y2="11"/>
          </svg>
          <span>@好友</span>
        </button>
        <button class="option-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"/>
            <circle cx="12" cy="10" r="3"/>
          </svg>
          <span>添加位置</span>
        </button>
        <button class="option-btn" @click="togglePrivacy">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 16v-4M12 8h.01"/>
          </svg>
          <span>{{ isPublic ? '公开' : '私密' }}</span>
        </button>
      </div>

      <!-- 发布须知 -->
      <div class="publish-notice">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
        </svg>
        <span>发布即表示你同意《社区规范》</span>
      </div>
    </main>

    <!-- 底部占位 -->
    <div class="bottom-placeholder"></div>

    <!-- 隐藏的文件输入 -->
    <input type="file" ref="fileInputRef" accept="image/*" multiple @change="handleFileChange" style="display: none" />

    <!-- Toast 提示 -->
    <Teleport to="body">
      <Transition name="toast">
        <div v-if="toast.show" class="toast" :class="toast.type">
          <svg v-if="toast.type === 'success'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
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
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from "vue";
import { useRouter } from "vue-router";
import { createPost } from "@/api/modules/community";
import { toErrorMessage } from "@/api/http";

const router = useRouter();

const categories = [
  { value: 'daily', label: '日常', color: '#ff9b7a' },
  { value: 'pet', label: '晒宠', color: '#9370db' },
  { value: 'qa', label: '问答', color: '#40e0d0' },
  { value: 'share', label: '种草', color: '#ff69b4' },
  { value: 'knowledge', label: '知识', color: '#3cb371' },
  { value: 'goods', label: '好物', color: '#ffb347' }
];

const selectedCategory = ref("daily");
const tagInput = ref("");
const fileInputRef = ref<HTMLInputElement | null>(null);
const titleTextareaRef = ref<HTMLTextAreaElement | null>(null);
const isPublic = ref(true);

const form = reactive({
  title: "",
  content: "",
  images: [] as string[],
  tags: [] as string[]
});

const recommendTags = ["新手养猫", "狗狗训练", "自制零食", "宠物摄影", "春季驱虫", "养兔指南", "布偶猫", "宠物健康", "猫咪用品", "萌宠瞬间"];

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

const autoResizeTitle = () => {
  if (titleTextareaRef.value) {
    titleTextareaRef.value.style.height = 'auto';
    titleTextareaRef.value.style.height = titleTextareaRef.value.scrollHeight + 'px';
  }
};

const triggerUpload = () => {
  fileInputRef.value?.click();
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

const toggleRecommendTag = (tag: string) => {
  if (form.tags.includes(tag)) {
    form.tags = form.tags.filter(t => t !== tag);
  } else if (form.tags.length < 5) {
    form.tags.push(tag);
  }
};

const togglePrivacy = () => {
  isPublic.value = !isPublic.value;
};

const showToast = (message: string, type: "success" | "error" = "success") => {
  toast.message = message;
  toast.type = type;
  toast.show = true;
  setTimeout(() => {
    toast.show = false;
  }, 2500);
};

const handlePublish = async () => {
  if (!canPublish.value) return;
  
  try {
    await createPost({
      title: form.title,
      content: form.content,
      category: selectedCategory.value,
      images: form.images
    });
    showToast("发布成功！", "success");
    setTimeout(() => {
      router.push("/community");
    }, 1500);
  } catch (e) {
    showToast(toErrorMessage(e), "error");
  }
};
</script>

<style scoped lang="scss">
.create-post-page {
  min-height: 100vh;
  background: linear-gradient(180deg, var(--surface-muted) 0%, var(--bg) 100%);
  padding-bottom: env(safe-area-inset-bottom);
}

.nav-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  padding-top: calc(12px + env(safe-area-inset-top));
  background: var(--surface);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border-warm);

  .nav-left { width: 60px; }

  .back-btn {
    width: 40px; height: 40px;
    display: flex; align-items: center; justify-content: center;
    background: var(--surface-muted);
    border: none; border-radius: 50%;
    cursor: pointer;
    svg { width: 22px; height: 22px; color: var(--text-heading); }
    &:hover { background: rgba(0,0,0,0.1); }
  }

  .nav-title { font-size: 17px; font-weight: 700; color: var(--text-heading); margin: 0; }

  .publish-btn {
    padding: 8px 20px;
    background: var(--surface-muted);
    color: var(--muted);
    border: 1px solid var(--border-warm);
    border-radius: 20px;
    font-size: 14px; font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    &.active {
      background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%);
      color: #fff;
      box-shadow: 0 4px 16px rgba(255,107,107,0.4);
      border-color: transparent;
    }
    &:hover.active {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255,107,107,0.5);
    }
    &:disabled { cursor: not-allowed; }
  }
}

.main-content {
  padding: 16px;
  padding-top: calc(16px + env(safe-area-inset-top) + 60px);
  max-width: 900px;
  margin: 0 auto;
}

.user-card {
  display: flex; align-items: center; gap: 14px;
  padding: 16px;
  background: var(--surface);
  border-radius: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border: 1px solid var(--border-warm);

  .user-avatar-wrapper {
    position: relative;
    .user-avatar { width: 52px; height: 52px; border-radius: 50%; border: 3px solid var(--surface); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
    .user-level-badge {
      position: absolute; bottom: -4px; right: -4px;
      display: flex; align-items: center; gap: 2px;
      padding: 2px 6px;
      background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
      color: #fff; border-radius: 10px;
      font-size: 10px; font-weight: 700;
      box-shadow: 0 2px 8px rgba(255,215,0,0.4);
      svg { width: 10px; height: 10px; }
    }
  }

  .user-info {
    flex: 1; display: flex; flex-direction: column; gap: 4px;
    .user-name { font-size: 16px; font-weight: 700; color: var(--text-heading); }
    .user-desc { font-size: 12px; color: var(--muted); }
  }
}

.category-selector {
  background: var(--surface);
  border-radius: 16px; padding: 16px; margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border: 1px solid var(--border-warm);

  .selector-label {
    display: flex; align-items: center; gap: 6px;
    font-size: 13px; font-weight: 600; color: var(--text-heading);
    margin-bottom: 12px;
    svg { width: 18px; height: 18px; color: var(--primary); }
  }

  .category-tabs {
    display: flex; flex-wrap: wrap; gap: 8px;
    .category-tab {
      display: flex; align-items: center; gap: 6px;
      padding: 8px 14px;
      background: var(--bg);
      border: 1.5px solid rgba(0,0,0,0.08);
      border-radius: 20px;
      font-size: 13px; font-weight: 600;
      color: var(--text); cursor: pointer;
      transition: all 0.2s ease;
      .tab-icon { 
        display: flex; 
        align-items: center; 
        justify-content: center; 
        width: 20px; 
        height: 20px; 
        font-size: 14px; 
        font-weight: 700; 
      }
      &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
      &.active { color: #fff; border-color: transparent; box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
    }
  }
}

.title-input-wrapper {
  position: relative;
  background: var(--surface);
  border-radius: 16px; padding: 16px; margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border: 1px solid var(--border-warm);

  .title-textarea {
    width: 100%; border: none; outline: none;
    font-size: 18px; font-weight: 700;
    color: var(--text-heading); resize: none;
    line-height: 1.5; background: transparent;
    &::placeholder { color: rgba(0,0,0,0.25); font-weight: 500; }
  }

  .title-count {
    position: absolute; right: 16px; bottom: 12px;
    font-size: 12px; color: var(--muted);
    &.warning { color: #ff6b6b; }
  }
}

.content-input-wrapper {
  position: relative;
  background: var(--surface);
  border-radius: 16px; padding: 16px; margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border: 1px solid var(--border-warm);

  .content-textarea {
    width: 100%; min-height: 160px;
    border: none; outline: none;
    font-size: 15px; color: var(--text);
    line-height: 1.8; resize: none; background: transparent;
    &::placeholder { color: rgba(0,0,0,0.25); }
  }

  .content-footer {
    display: flex; justify-content: flex-end;
    padding-top: 8px; border-top: 1px solid rgba(0,0,0,0.05);
    margin-top: 8px;
    .content-count { font-size: 12px; color: var(--muted); &.warning { color: #ff6b6b; } }
  }
}

.image-uploader { margin-bottom: 16px;
  .image-grid {
    display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; margin-bottom: 12px;
    .image-item {
      position: relative; aspect-ratio: 1; border-radius: 12px; overflow: hidden;
      img { width: 100%; height: 100%; object-fit: cover; }
      &.first-image { grid-column: span 2; grid-row: span 2; }
      .remove-btn {
        position: absolute; top: 6px; right: 6px;
        width: 24px; height: 24px;
        background: rgba(0,0,0,0.5); border: none; border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        cursor: pointer;
        svg { width: 12px; height: 12px; color: #fff; }
        &:hover { background: rgba(255,107,107,0.9); transform: scale(1.1); }
      }
      .cover-tag {
        position: absolute; bottom: 8px; left: 8px;
        display: flex; align-items: center; gap: 4px;
        padding: 4px 10px; background: rgba(0,0,0,0.6);
        border-radius: 12px; color: #fff;
        font-size: 11px; font-weight: 600;
        svg { width: 12px; height: 12px; }
      }
    }
  }

  .upload-area {
    background: var(--surface); border-radius: 16px; padding: 24px;
    border: 2px dashed var(--border-warm); cursor: pointer;
    &:hover { border-color: var(--primary); background: rgba(255,155,122,0.05); }
    .upload-content {
      display: flex; flex-direction: column; align-items: center; gap: 8px;
      .upload-icon {
        width: 48px; height: 48px;
        display: flex; align-items: center; justify-content: center;
        background: linear-gradient(135deg, rgba(255,155,122,0.1) 0%, rgba(91,185,140,0.1) 100%);
        border-radius: 12px;
        svg { width: 24px; height: 24px; color: var(--primary); }
      }
      .upload-text { font-size: 14px; font-weight: 600; color: var(--text-heading); }
      .upload-hint { font-size: 12px; color: var(--muted); }
    }
  }
}

.tags-section {
  background: var(--surface); border-radius: 16px; padding: 16px; margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  border: 1px solid var(--border-warm);

  .section-header {
    display: flex; align-items: center; gap: 6px;
    font-size: 13px; font-weight: 600; color: var(--text-heading);
    margin-bottom: 12px;
    svg { width: 18px; height: 18px; color: var(--primary); }
    .tag-tip { margin-left: auto; font-size: 11px; font-weight: 500; color: var(--muted); }
  }

  .tags-input-area {
    background: var(--bg); border-radius: 12px; padding: 12px; margin-bottom: 12px;
    .selected-tags {
      display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 8px;
      .selected-tag {
        display: inline-flex; align-items: center; gap: 4px;
        padding: 6px 10px;
        background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%);
        color: #fff; border-radius: 16px;
        font-size: 13px; font-weight: 600;
        button {
          background: none; border: none; padding: 0; cursor: pointer;
          display: flex; align-items: center; justify-content: center;
          svg { width: 14px; height: 14px; color: #fff; opacity: 0.8; }
          &:hover svg { opacity: 1; }
        }
      }
    }
    .tag-input-wrapper {
      display: flex; gap: 8px;
      .tag-input {
        flex: 1; padding: 8px; background: transparent; border: none; outline: none;
        font-size: 14px; color: var(--text);
        &::placeholder { color: var(--muted); }
      }
      .add-tag-btn {
        padding: 8px 16px; background: var(--primary); color: #fff;
        border: none; border-radius: 16px;
        font-size: 13px; font-weight: 600; cursor: pointer;
        &:hover { opacity: 0.9; }
      }
    }
  }

  .recommend-tags {
    display: flex; align-items: center; gap: 8px;
    .recommend-label { font-size: 12px; color: var(--muted); flex-shrink: 0; }
    .tags-scroll { display: flex; flex-wrap: wrap; gap: 6px;
      .recommend-tag {
        padding: 5px 12px; background: rgba(255,155,122,0.1);
        border: 1px solid transparent; border-radius: 14px;
        font-size: 12px; color: var(--primary); cursor: pointer;
        &:hover { background: rgba(255,155,122,0.2); }
        &.selected { background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%); color: #fff; }
      }
    }
  }
}

.extra-options {
  display: flex; gap: 12px; margin-bottom: 20px;
  .option-btn {
    flex: 1; display: flex; flex-direction: column; align-items: center; gap: 6px;
    padding: 16px; background: var(--surface);
    border: 1px solid var(--border-warm); border-radius: 16px;
    cursor: pointer; box-shadow: 0 2px 12px rgba(0,0,0,0.04);
    svg { width: 24px; height: 24px; color: var(--primary); }
    span { font-size: 12px; font-weight: 600; color: var(--text); }
    &:hover { transform: translateY(-2px); box-shadow: 0 4px 16px rgba(0,0,0,0.08); }
  }
}

.publish-notice {
  display: flex; align-items: center; justify-content: center; gap: 6px;
  padding: 12px; color: var(--muted); font-size: 12px;
  svg { width: 16px; height: 16px; }
}

.bottom-placeholder { height: 20px; }

.toast {
  position: fixed; top: 80px; left: 50%; transform: translateX(-50%);
  display: flex; align-items: center; gap: 8px;
  padding: 14px 28px;
  background: linear-gradient(135deg, #ff9b7a 0%, #ff6b6b 100%);
  color: #fff; border-radius: 24px;
  font-size: 14px; font-weight: 600; z-index: 1000;
  box-shadow: 0 8px 32px rgba(255,107,107,0.4);
  svg { width: 18px; height: 18px; }
  &.error { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%); }
}

.toast-enter-active, .toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateX(-50%) translateY(-20px); }

@media (max-width: 480px) {
  .category-tabs .category-tab { padding: 6px 12px; font-size: 12px; .tab-icon { display: none; } }
  .extra-options { gap: 8px; .option-btn { padding: 12px; span { font-size: 11px; } } }
}
</style>
