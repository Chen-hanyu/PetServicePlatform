<template>
  <div class="favorites-page">
    <!-- 顶部导航 -->
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
          <path d="M15 18l-6-6 6-6"/>
        </svg>
      </button>
      <h1 class="page-title">我的收藏</h1>
      <div class="header-spacer"></div>
    </header>

    <!-- 收藏列表 -->
    <main class="page-content">
      <DataState :loading="loading" :empty="favorites.length === 0" empty-text="暂无收藏内容，快去收藏喜欢的帖子吧">
        <div class="favorites-list">
          <article
            v-for="item in favorites"
            :key="item.id"
            class="favorite-item"
            @click="goToPost(item.id)"
          >
            <div class="item-image">
              <img
                :src="item.cover_url || 'https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=400&q=80'"
                :alt="item.title"
              />
            </div>
            <div class="item-content">
              <h3 class="item-title">{{ item.title }}</h3>
              <p class="item-excerpt">{{ item.excerpt || item.content?.slice(0, 60) + '...' || '暂无描述' }}</p>
              <div class="item-meta">
                <span class="author">
                  <img :src="item.author?.avatar_url || 'https://api.dicebear.com/7.x/avataaars/svg?seed=Felix'" />
                  {{ item.author?.nickname || '匿名用户' }}
                </span>
                <span class="stats">
                  ❤️ {{ item.like_count || 0 }} · 💬 {{ item.comment_count || 0 }}
                </span>
              </div>
            </div>
            <button class="remove-btn" @click.stop="removeFavorite(item.id)">
              <svg viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
              </svg>
            </button>
          </article>
        </div>
      </DataState>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";

const router = useRouter();
const loading = ref(true);

interface FavoriteItem {
  id: number;
  title: string;
  content?: string;
  excerpt?: string;
  cover_url?: string;
  like_count?: number;
  comment_count?: number;
  author?: {
    nickname: string;
    avatar_url?: string;
  };
}

const favorites = ref<FavoriteItem[]>([]);

const goBack = () => {
  router.back();
};

const goToPost = (id: number) => {
  router.push(`/community/post/${id}`);
};

const removeFavorite = (id: number) => {
  favorites.value = favorites.value.filter(item => item.id !== id);
};

onMounted(() => {
  setTimeout(() => {
    favorites.value = [
      {
        id: 1,
        title: "新手养猫必看！这些注意事项你都知道吗？",
        excerpt: "养猫前的准备工作、接猫注意事项、幼猫喂养指南...",
        cover_url: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=400&q=80",
        like_count: 256,
        comment_count: 32,
        author: { nickname: "喵星人", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=1" }
      },
      {
        id: 2,
        title: "自制狗狗零食，简单又健康！",
        excerpt: "分享几款简单易做的狗狗零食，让你的毛孩子吃得健康...",
        cover_url: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=400&q=80",
        like_count: 189,
        comment_count: 45,
        author: { nickname: "铲屎官小李", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=2" }
      },
      {
        id: 3,
        title: "猫咪打疫苗全攻略，新手必读",
        excerpt: "猫咪需要接种哪些疫苗？什么时候打？有哪些注意事项...",
        cover_url: "https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=400&q=80",
        like_count: 324,
        comment_count: 67,
        author: { nickname: "宠物医生王大夫", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=3" }
      },
      {
        id: 4,
        title: "如何训练狗狗定点上厕所",
        excerpt: "狗狗乱拉乱尿？教你几招轻松解决...",
        cover_url: "https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&w=400&q=80",
        like_count: 156,
        comment_count: 28,
        author: { nickname: "训犬师老张", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=4" }
      },
      {
        id: 5,
        title: "布偶猫饲养心得分享",
        excerpt: "养布偶猫两年，总结了一些经验和心得...",
        cover_url: "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=400&q=80",
        like_count: 412,
        comment_count: 89,
        author: { nickname: "猫奴日记", avatar_url: "https://api.dicebear.com/7.x/avataaars/svg?seed=5" }
      }
    ];
    loading.value = false;
  }, 500);
});
</script>

<style scoped lang="scss">
.favorites-page {
  min-height: 100vh;
  background: var(--bg);
}

.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: var(--surface);
  border-bottom: 1px solid var(--border-warm);
  backdrop-filter: blur(20px);

  .back-btn {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--surface-muted);
    border: 1px solid var(--border-warm);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.2s ease;

    svg {
      width: 20px;
      height: 20px;
      color: var(--text-heading);
    }

    &:hover {
      transform: scale(1.05);
      background: var(--primary);
      border-color: var(--primary);

      svg {
        color: #fff;
      }
    }
  }

  .page-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0;
  }

  .header-spacer {
    width: 40px;
  }
}

.page-content {
  padding: 20px;
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.favorite-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--surface);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }

  .item-image {
    width: 120px;
    height: 100px;
    border-radius: 12px;
    overflow: hidden;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .item-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 0;

    .item-title {
      font-size: 16px;
      font-weight: 700;
      color: var(--text-heading);
      margin: 0 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-excerpt {
      font-size: 13px;
      color: var(--muted);
      margin: 0 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 12px;
      color: var(--muted-soft);

      .author {
        display: flex;
        align-items: center;
        gap: 6px;

        img {
          width: 20px;
          height: 20px;
          border-radius: 50%;
        }
      }

      .stats {
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
  }

  .remove-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--surface-muted);
    border: none;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.2s ease;
    color: var(--primary);

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: var(--primary);
      color: #fff;
      transform: scale(1.1);
    }
  }
}

@media (max-width: 480px) {
  .favorite-item {
    flex-direction: column;

    .item-image {
      width: 100%;
      height: 160px;
    }
  }
}
</style>
