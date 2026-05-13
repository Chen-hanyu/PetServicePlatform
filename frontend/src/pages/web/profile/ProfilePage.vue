<template>
  <section class="profile-hub">
    <!-- 主内容区 -->
    <main class="main-content">
      <div class="content-grid">
        <!-- 左侧边栏 -->
        <aside class="sidebar">
          <!-- 用户信息卡片 -->
          <section class="user-card">
            <div class="avatar-wrap">
              <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.nickname" />
              <button class="avatar-edit" @click="editAvatar">
                <svg viewBox="0 0 20 20" fill="currentColor">
                  <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
                </svg>
              </button>
            </div>
            <h2 class="user-name">{{ userInfo.nickname }}</h2>
            <p class="user-bio">{{ userInfo.bio }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <p class="stat-num">{{ stats.dynamic_count }}</p>
                <p class="stat-label">动态</p>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <p class="stat-num">{{ stats.like_count }}</p>
                <p class="stat-label">获赞</p>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <p class="stat-num">{{ stats.follow_count }}</p>
                <p class="stat-label">关注</p>
              </div>
            </div>
          </section>

          <!-- 导航菜单 -->
          <nav class="nav-menu">
            <ul>
              <li>
                <RouterLink to="/profile/settings" class="nav-item active">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                    </svg>
                  </span>
                  <span class="nav-text">个人资料</span>
                  <span class="nav-arrow">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M9 5l7 7-7 7"/>
                    </svg>
                  </span>
                </RouterLink>
              </li>
              <li>
                <RouterLink to="/profile/posts" class="nav-item">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                    </svg>
                  </span>
                  <span class="nav-text">我的动态</span>
                </RouterLink>
              </li>
              <li>
                <RouterLink to="/profile/favorites" class="nav-item">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M19 21l-7-5-7 5V5a2 2 0 012-2h10a2 2 0 012 2z"/>
                    </svg>
                  </span>
                  <span class="nav-text">我的收藏</span>
                </RouterLink>
              </li>
              <li>
                <RouterLink to="/profile/orders" class="nav-item">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
                    </svg>
                  </span>
                  <span class="nav-text">订单中心</span>
                </RouterLink>
              </li>
              <li>
                <RouterLink to="/profile/applications" class="nav-item">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                    </svg>
                  </span>
                  <span class="nav-text">领养申请</span>
                </RouterLink>
              </li>
              <li>
                <RouterLink to="/profile/messages" class="nav-item">
                  <span class="nav-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
                    </svg>
                  </span>
                  <span class="nav-text">消息通知</span>
                  <span class="nav-badge">3</span>
                </RouterLink>
              </li>
            </ul>
          </nav>
        </aside>

        <!-- 右侧内容区 -->
        <div class="content-area">
          <!-- 我的宠物区域 -->
          <section class="section pets-section">
            <div class="section-header">
              <h3 class="section-title">
                <span class="title-accent"></span>
                我的宠物
              </h3>
              <RouterLink to="/profile/pets" class="add-btn">
                添加宠物
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 4v16m8-8H4"/>
                </svg>
              </RouterLink>
            </div>
            <div class="pets-scroll">
              <article v-for="pet in pets" :key="pet.id" class="pet-card">
                <img :src="pet.avatar || defaultPetAvatar" :alt="pet.name" class="pet-avatar" />
                <div class="pet-info">
                  <h4 class="pet-name">{{ pet.name }}</h4>
                  <p class="pet-desc">{{ pet.breed || pet.type }} · {{ pet.ageText }} · {{ pet.genderText }}</p>
                  <div class="pet-tags">
                    <span class="tag tag-health">档案</span>
                  </div>
                </div>
              </article>
              <!-- 添加更多卡片 -->
              <div class="pet-card add-card" @click="goToAddPet">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                </svg>
                <span>添加更多</span>
              </div>
            </div>
          </section>

          <!-- 内容网格 -->
          <div class="content-grid-inner">
            <!-- 最近动态 -->
            <section class="section activity-section">
              <h3 class="section-title-sm">最近动态</h3>
              <div class="activity-list">
                <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
                  <div :class="['activity-dot', `dot-${activity.color}`]"></div>
                  <p class="activity-text">
                    {{ activity.text }}
                    <span class="highlight">{{ activity.highlight }}</span>
                  </p>
                  <p class="activity-time">{{ activity.time }}</p>
                </div>
              </div>
              <RouterLink to="/profile/posts" class="view-all-btn">查看全部动态</RouterLink>
            </section>

            <!-- 成长时间轴 -->
            <section class="section timeline-section">
              <div class="timeline-header">
                <h3 class="section-title-sm">成长时间轴</h3>
                <div class="timeline-controls">
                  <select class="timeline-select">
                    <option v-for="pet in pets" :key="pet.id">{{ pet.name }}</option>
                  </select>
                  <button class="icon-btn-sm">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"/>
                    </svg>
                  </button>
                </div>
              </div>
              <div v-if="timelinePet" class="timeline-preview">
                <div class="timeline-photo">
                  <div class="photo-label">{{ timelinePet.birthdayLabel }}</div>
                  <img :src="timelinePet.avatar || defaultPetAvatar" :alt="timelinePet.name" />
                </div>
                <div class="timeline-text">
                  <p class="timeline-title"><span class="pet-name-sm">{{ timelinePet.name }}</span> - 宠物成长档案</p>
                  <p class="timeline-desc">{{ timelinePet.description || '完善生日、体重和疫苗记录后，可以在这里查看更完整的成长时间线。' }}</p>
                  <div class="timeline-link">
                    <span>查看时间轴详情</span>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14 5l7 7m0 0l-7 7m7-7H3"/>
                    </svg>
                  </div>
                </div>
              </div>
              <!-- S曲线时间轴 -->
              <div class="timeline-path">
                <svg viewBox="0 0 400 100" preserveAspectRatio="none">
                  <defs>
                    <linearGradient id="sCurveGradient" x1="0%" x2="100%" y1="0%" y2="0%">
                      <stop offset="0%" stop-color="var(--primary)"/>
                      <stop offset="50%" stop-color="#FFD66B"/>
                      <stop offset="100%" stop-color="var(--primary)"/>
                    </linearGradient>
                  </defs>
                  <path d="M 40 50 Q 120 10, 200 50 T 360 50" stroke="url(#sCurveGradient)" stroke-width="8" fill="none" stroke-linecap="round"/>
                </svg>
                <div v-if="timelinePet" class="milestones">
                  <div class="milestone top">
                    <div class="milestone-icon">🐣</div>
                    <div class="milestone-label">出生</div>
                  </div>
                  <div class="milestone bottom">
                    <div class="milestone-icon">🏠</div>
                    <div class="milestone-label">回家</div>
                  </div>
                  <div class="milestone top">
                    <div class="milestone-icon accent">🎂</div>
                    <div class="milestone-label accent">{{ timelinePet.ageText }}</div>
                  </div>
                  <div class="milestone bottom">
                    <div class="milestone-icon muted">🦴</div>
                    <div class="milestone-label muted">2岁</div>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>

        <!-- 我的收藏 - 与商城订单对齐 -->
        <section class="section favorites-section">
          <div class="section-header">
            <h3 class="section-title-sm">我的收藏</h3>
            <RouterLink to="/profile/favorites" class="view-link">查看全部 &gt;</RouterLink>
          </div>
          <div class="favorites-grid">
            <RouterLink
              v-for="item in favoritePosts"
              :key="item.id"
              :to="`/community/post/${item.id}`"
              class="favorite-card"
            >
              <div class="favorite-image">
                <img :src="item.cover_url || item.image" :alt="item.title" />
              </div>
              <div class="favorite-info">
                <h4 class="favorite-title">{{ item.title }}</h4>
                <p class="favorite-author">{{ item.author?.nickname || '匿名用户' }}</p>
              </div>
            </RouterLink>
            <div v-if="favoritePosts.length === 0" class="favorites-empty">
              <p>暂无收藏内容</p>
            </div>
          </div>
        </section>

        <!-- 商城订单 - 与左侧边栏对齐 -->
        <section class="section order-section">
          <div class="section-header">
            <h3 class="section-title-sm">商城订单</h3>
            <RouterLink to="/profile/orders" class="view-link">全部订单 &gt;</RouterLink>
          </div>
          <div class="order-icons">
            <RouterLink v-for="order in orderTypes" :key="order.key" :to="`/profile/orders?tab=${order.key}`" class="order-icon-item">
              <div :class="['order-icon-wrap', { 'has-badge': order.badge }]">
                <div class="order-icon-bg">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" v-html="order.svg"></svg>
                </div>
                <span v-if="order.badge" class="badge">{{ order.badge }}</span>
              </div>
              <p class="order-label">{{ order.label }}</p>
            </RouterLink>
          </div>
        </section>
      </div>
    </main>

  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { fetchMyPets } from "@/api/modules/pet";
import { fetchOverview } from "@/api/modules/profile";
import { fetchMyFavorites } from "@/api/modules/community";
import type { PetProfile } from "@/types/pet";
import type { PostSummary } from "@/types/community";

const router = useRouter();
const auth = useAuthStore();

const defaultAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80";
const defaultPetAvatar = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=200&q=80";

type DashboardPet = PetProfile & {
  avatar?: string;
  ageText: string;
  genderText: string;
  birthdayLabel: string;
};

type FavoritePost = {
  id: number;
  title: string;
  cover_url?: string;
  image?: string;
  author?: {
    nickname?: string;
  };
};

type RecentActivity = {
  text: string;
  highlight: string;
  time: string;
  color: "primary" | "accent";
};

const userInfo = reactive({
  nickname: auth.user?.nickname || "未命名用户",
  bio: "还没有填写个人简介",
  avatar: auth.user?.avatar_url || defaultAvatar
});

const stats = reactive({
  dynamic_count: 0,
  like_count: 0,
  follow_count: 0,
  pet_count: 0,
  order_count: 0,
  booking_count: 0
});

const pets = ref<DashboardPet[]>([]);
const timelinePet = computed(() => pets.value[0] || null);

const favoritePosts = ref<FavoritePost[]>([]);
const recentActivities = ref<RecentActivity[]>([]);

const orderTypes = reactive([
  {
    key: "pending",
    label: "待付款",
    badge: 1,
    svg: '<path d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"/>'
  },
  {
    key: "shipping",
    label: "待发货",
    badge: 0,
    svg: '<path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>'
  },
  {
    key: "receiving",
    label: "待收货",
    badge: 0,
    svg: '<path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>'
  },
  {
    key: "review",
    label: "待评价",
    badge: 0,
    svg: '<path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>'
  },
  {
    key: "afterSale",
    label: "退换/售后",
    badge: 0,
    svg: '<path d="M16 15v-1a4 4 0 00-4-4H8m0 0l3 3m-3-3l3-3m9 14V5a2 2 0 00-2-2H6a2 2 0 00-2 2v16l4-2 4 2 4-2 4 2z"/>'
  }
]);

const editAvatar = () => {
  alert("头像上传功能开发中...");
};

const goToAddPet = () => {
  router.push("/profile/pets?action=add");
};

function getAgeText(birthday?: string) {
  if (!birthday) return "年龄未知";
  const birth = new Date(birthday);
  if (Number.isNaN(birth.getTime())) return "年龄未知";
  const now = new Date();
  let years = now.getFullYear() - birth.getFullYear();
  const hasHadBirthday =
    now.getMonth() > birth.getMonth() ||
    (now.getMonth() === birth.getMonth() && now.getDate() >= birth.getDate());
  if (!hasHadBirthday) years -= 1;
  return years > 0 ? `${years}岁` : "未满1岁";
}

function getGenderText(gender?: string) {
  if (gender === "MALE" || gender === "公") return "男宝";
  if (gender === "FEMALE" || gender === "母") return "女宝";
  return "性别未知";
}

function getBirthdayLabel(birthday?: string) {
  return birthday ? birthday.slice(0, 7) : "未记录";
}

function toDashboardPet(pet: PetProfile): DashboardPet {
  return {
    ...pet,
    avatar: pet.avatar_url,
    ageText: getAgeText(pet.birthday),
    genderText: getGenderText(pet.gender),
    birthdayLabel: getBirthdayLabel(pet.birthday)
  };
}

async function loadProfile() {
  const [overview, petList, favResult] = await Promise.all([
    fetchOverview(),
    fetchMyPets(),
    fetchMyFavorites({ page: 1, page_size: 4 }).catch(() => ({ list: [] }))
  ]);
  userInfo.nickname = overview.user.nickname || auth.user?.nickname || "未命名用户";
  userInfo.bio = overview.user.bio || "还没有填写个人简介";
  userInfo.avatar = overview.user.avatar_url || defaultAvatar;
  stats.dynamic_count = overview.post_count;
  stats.like_count = overview.favorite_count;
  stats.follow_count = overview.unread_message_count;
  stats.pet_count = overview.pet_count;
  stats.order_count = overview.order_count;
  stats.booking_count = overview.booking_count;
  pets.value = petList.map(toDashboardPet);

  // 加载收藏列表（卡片展示前4条）
  favoritePosts.value = (favResult.list || []).map((item: PostSummary) => ({
    id: item.id,
    title: item.title,
    cover_url: item.cover_url,
    image: item.cover_url,
    author: item.author ? { nickname: item.author.nickname } : undefined
  }));

  // 生成最近动态（基于用户数据）
  const now = new Date();
  const timeStr = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`;
  const activities: RecentActivity[] = [];
  if (overview.post_count > 0) {
    activities.push({
      text: "发布了",
      highlight: `${overview.post_count} 条新动态`,
      time: "今天 " + timeStr,
      color: "primary"
    });
  }
  if (overview.favorite_count > 0) {
    activities.push({
      text: "收藏了",
      highlight: `${overview.favorite_count} 篇内容`,
      time: "今天 " + timeStr,
      color: "accent"
    });
  }
  if (overview.pet_count > 0) {
    activities.push({
      text: "记录了",
      highlight: `${overview.pet_count} 只宠物`,
      time: "今天 " + timeStr,
      color: "primary"
    });
  }
  if (activities.length === 0) {
    activities.push({
      text: "还没有动态，快去",
      highlight: "探索社区",
      time: "现在",
      color: "primary"
    });
  }
  recentActivities.value = activities;
}

onMounted(() => {
  void loadProfile();
});
</script>

<style scoped lang="scss">
.profile-hub {
  min-height: 100vh;
  background-color: var(--bg);
  display: flex;
  flex-direction: column;
}

// 主内容区
.main-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 40px;
  flex: 1;
}

.content-grid {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 32px;
  align-items: start;
}

// 左侧边栏
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// 用户信息卡片
.user-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 24px;
  text-align: center;
  box-shadow: var(--shadow);

  .avatar-wrap {
    position: relative;
    display: inline-block;
    margin-bottom: 16px;

    img {
      width: 96px;
      height: 96px;
      border-radius: 50%;
      border: 4px solid var(--surface-muted);
      object-fit: cover;
    }

    .avatar-edit {
      position: absolute;
      bottom: 0;
      right: 0;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      border: none;
      background: var(--primary);
      color: #fff;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(255, 155, 122, 0.4);

      svg {
        width: 14px;
        height: 14px;
      }

      &:hover {
        transform: scale(1.1);
      }
    }
  }

  .user-name {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 4px;
  }

  .user-bio {
    font-size: 14px;
    color: var(--muted-soft);
    margin: 0 0 16px;
  }

  .user-stats {
    display: flex;
    justify-content: center;
    gap: 16px;
    border-top: 1px solid var(--border-warm);
    padding-top: 16px;
    margin-top: 16px;

    .stat-item {
      text-align: center;

      .stat-num {
        font-size: 18px;
        font-weight: 700;
        color: var(--text-heading);
        margin: 0;
      }

      .stat-label {
        font-size: 12px;
        color: var(--muted-soft);
        margin: 4px 0 0;
      }
    }

    .stat-divider {
      width: 1px;
      height: 32px;
      background: var(--border-warm);
    }
  }
}

// 导航菜单
.nav-menu {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow);

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .nav-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px;
    border-radius: var(--radius-md);
    color: var(--muted);
    text-decoration: none;
    transition: all 0.2s;

    &:hover {
      background: var(--surface-muted);
    }

    &.active {
      background: var(--surface-muted);
      color: var(--primary);
      font-weight: 500;
    }

    .nav-icon {
      display: flex;
      align-items: center;

      svg {
        width: 20px;
        height: 20px;
      }
    }

    .nav-text {
      flex: 1;
      margin-left: 12px;
    }

    .nav-arrow svg {
      width: 16px;
      height: 16px;
    }

    .nav-badge {
      background: #E97A7A;
      color: #fff;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 10px;
    }
  }
}

// 右侧内容区
.content-area {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

// 我的收藏 - 跨越整个宽度
.favorites-section {
  grid-column: 1 / -1;
}

// 商城订单 - 跨越整个宽度
.order-section {
  grid-column: 1 / -1;
}

.section {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-heading);
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;

  .title-accent {
    width: 8px;
    height: 24px;
    background: var(--primary);
    border-radius: 4px;
  }
}

.section-title-sm {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-heading);
  margin: 0 0 24px;
}

.add-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--primary);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }

  svg {
    width: 16px;
    height: 16px;
  }
}

.view-link {
  color: var(--muted-soft);
  font-size: 12px;
  text-decoration: none;

  &:hover {
    color: var(--primary);
  }
}

// 收藏卡片网格
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.favorite-card {
  display: block;
  text-decoration: none;
  color: inherit;
  border-radius: 12px;
  overflow: hidden;
  background: var(--bg);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow);
  }

  .favorite-image {
    width: 100%;
    height: 100px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .favorite-info {
    padding: 12px;

    .favorite-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-heading);
      margin: 0 0 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .favorite-author {
      font-size: 12px;
      color: var(--muted);
      margin: 0;
    }
  }
}

.favorites-empty {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px;
  color: var(--muted);
}

// 宠物滚动区域
.pets-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 16px;
  -ms-overflow-style: none;
  scrollbar-width: none;

  &::-webkit-scrollbar {
    display: none;
  }
}

.pet-card {
  min-width: 280px;
  background: var(--surface);
  border-radius: var(--radius-lg);
  padding: 16px;
  border: 1px solid transparent;
  box-shadow: var(--shadow);
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: var(--primary);
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }

  .pet-avatar {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    object-fit: cover;
    background: var(--surface-muted);
  }

  .pet-info {
    flex: 1;

    .pet-name {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-heading);
      margin: 0 0 4px;
    }

    .pet-desc {
      font-size: 12px;
      color: var(--muted-soft);
      margin: 0 0 8px;
    }

    .pet-tags {
      display: flex;
      gap: 8px;

      .tag {
        padding: 2px 8px;
        border-radius: 10px;
        font-size: 10px;
        font-weight: 500;

        &.tag-vaccine {
          background: var(--surface-muted);
          color: var(--primary);
        }

        &.tag-health {
          background: rgba(255, 214, 107, 0.1);
          color: var(--accent);
        }
      }
    }
  }

  &.add-card {
    min-width: 200px;
    border: 2px dashed var(--border-warm);
    background: transparent;
    flex-direction: column;
    justify-content: center;
    color: var(--muted-soft);

    svg {
      width: 32px;
      height: 32px;
      margin-bottom: 8px;
    }

    span {
      font-size: 14px;
      font-weight: 500;
    }

    &:hover {
      border-color: var(--primary);
      color: var(--primary);
      transform: none;
      box-shadow: none;
    }
  }
}

// 内容网格
.content-grid-inner {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

// 最近动态
.activity-list {
  position: relative;
  padding-left: 32px;

  &::before {
    content: '';
    position: absolute;
    left: 7px;
    top: 16px;
    bottom: 16px;
    width: 2px;
    background: var(--border-warm);
  }

  .activity-item {
    position: relative;
    padding: 16px 0;
    border-bottom: 1px solid transparent;

    &:last-child {
      border-bottom: none;
    }

    .activity-dot {
      position: absolute;
      left: -26px;
      top: 20px;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      border: 4px solid var(--surface);
      background: #fff;
      z-index: 1;

      &.dot-primary {
        border-color: var(--primary);
      }

      &.dot-accent {
        border-color: var(--accent);
      }
    }

    .activity-text {
      font-size: 14px;
      color: var(--text-heading);
      margin: 0 0 4px;

      .highlight {
        color: var(--primary);
        font-weight: 500;
      }
    }

    .activity-time {
      font-size: 12px;
      color: var(--muted-soft);
      margin: 0;
    }
  }
}

.view-all-btn {
  display: block;
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--surface-muted);
  color: var(--muted);
  font-size: 14px;
  text-align: center;
  text-decoration: none;
  cursor: pointer;
  margin-top: 16px;
  transition: all 0.2s;

  &:hover {
    background: var(--surface-muted-hover);
    color: var(--primary);
  }
}

// 成长时间轴
.timeline-section {
  .timeline-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
  }

  .timeline-controls {
    display: flex;
    gap: 8px;
    align-items: center;

    .timeline-select {
      border: none;
      background: var(--surface-muted);
      border-radius: 20px;
      padding: 4px 12px;
      font-size: 12px;
      color: var(--muted);
      cursor: pointer;

      &:focus {
        outline: none;
        box-shadow: 0 0 0 2px var(--primary);
      }
    }

    .icon-btn-sm {
      padding: 4px;
      border: none;
      background: none;
      color: var(--primary);
      cursor: pointer;
      border-radius: 50%;
      transition: background 0.2s;

      &:hover {
        background: rgba(255, 155, 122, 0.1);
      }

      svg {
        width: 16px;
        height: 16px;
      }
    }
  }
}

.timeline-preview {
  display: flex;
  gap: 16px;
  align-items: flex-start;

  .timeline-photo {
    position: relative;
    width: 112px;
    height: 128px;
    background: var(--surface);
    padding: 8px;
    padding-bottom: 24px;
    border-radius: 4px;
    transform: rotate(-2deg);
    box-shadow: var(--shadow);
    border: 1px solid rgba(228, 236, 233, 0.5);
    flex-shrink: 0;

    .photo-label {
      position: absolute;
      top: 4px;
      left: 4px;
      background: var(--primary);
      color: #fff;
      font-size: 10px;
      padding: 2px 8px;
      border-radius: 2px;
      z-index: 1;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 2px;
      background: var(--surface-muted);
    }
  }

  .timeline-text {
    flex: 1;

    .timeline-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-heading);
      margin: 0 0 4px;
      display: -webkit-box;
      -webkit-line-clamp: 1;
      -webkit-box-orient: vertical;
      overflow: hidden;

      .pet-name-sm {
        font-size: 18px;
        font-weight: 700;
      }
    }

    .timeline-desc {
      font-size: 12px;
      color: var(--muted-soft);
      margin: 0;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .timeline-link {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 8px;
      color: var(--primary);

      span {
        font-size: 12px;
        font-weight: 500;
      }

      svg {
        width: 12px;
        height: 12px;
      }
    }
  }
}

// S曲线时间轴
.timeline-path {
  position: relative;
  height: 112px;
  margin-top: 16px;
  overflow: visible;

  svg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    overflow: visible;
  }

  .milestones {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 16px;

    .milestone {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;

      &.top {
        margin-top: -16px;
      }

      &.bottom {
        margin-top: 32px;
      }

      .milestone-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: #fff;
        border: 2px solid var(--primary);
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: var(--shadow);
        font-size: 20px;

        &.accent {
          border-color: var(--accent);
        }

        &.muted {
          border-color: var(--border-warm);
        }
      }

      .milestone-label {
        background: rgba(255, 155, 122, 0.1);
        padding: 2px 8px;
        border-radius: 10px;
        font-size: 10px;
        font-weight: 700;
        color: var(--primary);

        &.accent {
          background: rgba(255, 214, 107, 0.1);
          color: var(--accent);
        }

        &.muted {
          background: var(--surface-muted);
          color: var(--muted-soft);
        }
      }
    }
  }
}

// 商城订单
.order-icons {
  display: flex;
  justify-content: space-between;
  text-align: center;

  .order-icon-item {
    flex: 1;
    cursor: pointer;
    text-decoration: none;
    display: flex;
    flex-direction: column;
    align-items: center;

    &:hover {
      .order-icon-bg {
        background: var(--primary);
        color: #fff;
      }

      .order-label {
        color: var(--text-heading);
      }
    }

    .order-icon-wrap {
      position: relative;
      display: inline-block;

      &.has-badge {
        .order-icon-bg {
          background: var(--surface-muted);
        }
      }

      .order-icon-bg {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        background: var(--surface-muted);
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto;
        color: var(--muted);
        transition: all 0.2s;

        svg {
          width: 24px;
          height: 24px;
        }
      }

      .badge {
        position: absolute;
        top: -4px;
        right: -4px;
        width: 16px;
        height: 16px;
        border-radius: 50%;
        background: #E97A7A;
        color: #fff;
        font-size: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .order-label {
      font-size: 12px;
      color: var(--muted);
      margin-top: 8px;
      transition: color 0.2s;
    }
  }
}

// 响应式
@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .sidebar {
    order: -1;
    flex-direction: row;
    overflow-x: auto;

    .user-card,
    .nav-menu {
      min-width: 280px;
    }
  }

  .content-grid-inner {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px;
  }

  .pets-scroll {
    gap: 12px;

    .pet-card {
      min-width: 240px;
    }
  }
}
</style>
