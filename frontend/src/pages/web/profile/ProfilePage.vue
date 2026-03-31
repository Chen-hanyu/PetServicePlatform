<template>
  <section class="profile-hub">
    <!-- User Hero Banner -->
    <div class="user-hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="avatar-wrap">
          <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.nickname" />
          <button class="avatar-badge" @click="editAvatar" title="更换头像">✎</button>
        </div>
        <div class="user-detail">
          <h1>{{ userInfo.nickname }}</h1>
          <p class="bio">{{ userInfo.bio || "还没有个人简介" }}</p>
          <div class="user-meta">
            <span v-if="userInfo.phone">📱 {{ userInfo.phone }}</span>
            <span v-if="userInfo.email">✉️ {{ userInfo.email }}</span>
          </div>
        </div>
        <RouterLink to="/profile/settings" class="settings-btn">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
          设置
        </RouterLink>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="stats-row">
      <RouterLink to="/profile/pets" class="stat-card">
        <div class="stat-icon">🐾</div>
        <div class="stat-num">{{ stats.pet_count }}</div>
        <div class="stat-label">我的宠物</div>
        <div class="stat-arrow">›</div>
      </RouterLink>
      <RouterLink to="/profile/orders" class="stat-card">
        <div class="stat-icon">📦</div>
        <div class="stat-num">{{ stats.order_count }}</div>
        <div class="stat-label">我的订单</div>
        <div class="stat-arrow">›</div>
      </RouterLink>
      <RouterLink to="/profile/bookings" class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-num">{{ stats.booking_count }}</div>
        <div class="stat-label">我的预约</div>
        <div class="stat-arrow">›</div>
      </RouterLink>
      <RouterLink to="/profile/settings" class="stat-card">
        <div class="stat-icon">⚙️</div>
        <div class="stat-num">—</div>
        <div class="stat-label">账户设置</div>
        <div class="stat-arrow">›</div>
      </RouterLink>
    </div>

    <!-- Action Buttons -->
    <div class="action-grid">
      <RouterLink to="/profile/pets" class="action-card">
        <div class="action-icon pets-icon">🐱</div>
        <div class="action-text">
          <span class="action-title">添加宠物</span>
          <span class="action-desc">记录爱宠信息</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
      <RouterLink to="/profile/orders" class="action-card">
        <div class="action-icon orders-icon">🛒</div>
        <div class="action-text">
          <span class="action-title">查看订单</span>
          <span class="action-desc">追踪消费记录</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
      <RouterLink to="/profile/bookings" class="action-card">
        <div class="action-icon bookings-icon">💇</div>
        <div class="action-text">
          <span class="action-title">预约服务</span>
          <span class="action-desc">预约美容、医疗</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
      <RouterLink to="/adoption" class="action-card">
        <div class="action-icon adoption-icon">❤️</div>
        <div class="action-text">
          <span class="action-title">领养申请</span>
          <span class="action-desc">给流浪动物一个家</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
      <RouterLink to="/community" class="action-card">
        <div class="action-icon community-icon">📝</div>
        <div class="action-text">
          <span class="action-title">我的帖子</span>
          <span class="action-desc">管理社区内容</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
      <RouterLink to="/profile/settings" class="action-card">
        <div class="action-icon settings-icon">🔐</div>
        <div class="action-text">
          <span class="action-title">账户安全</span>
          <span class="action-desc">密码与绑定</span>
        </div>
        <span class="action-arrow">›</span>
      </RouterLink>
    </div>

    <!-- Recent Activity -->
    <div class="recent-section">
      <div class="section-header">
        <h2>最近动态</h2>
      </div>
      <div class="activity-list">
        <article class="activity-item">
          <div class="activity-icon">📦</div>
          <div class="activity-content">
            <p class="activity-text">订单 <strong>DD20260330001</strong> 已完成</p>
            <span class="activity-time">2026-03-30 14:30</span>
          </div>
        </article>
        <article class="activity-item">
          <div class="activity-icon">📅</div>
          <div class="activity-content">
            <p class="activity-text">成功预约 <strong>爪爪宠物美容</strong> 的精致洗护套餐</p>
            <span class="activity-time">2026-03-29 10:20</span>
          </div>
        </article>
        <article class="activity-item">
          <div class="activity-icon">🐾</div>
          <div class="activity-content">
            <p class="activity-text">添加了新宠物 <strong>豆豆</strong></p>
            <span class="activity-time">2026-03-28 16:05</span>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import { useAuthStore } from "@/store/auth";

const defaultAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80";

const auth = useAuthStore();

const userInfo = reactive({
  nickname: auth.user?.nickname || "萌宠达人",
  bio: "热爱小动物，养宠5年经验",
  phone: "138****8888",
  email: "pet@lover.com",
  avatar: auth.user?.avatar_url || defaultAvatar
});

const stats = reactive({
  pet_count: 3,
  order_count: 5,
  booking_count: 2
});

const editAvatar = () => {
  alert("头像上传功能开发中...");
};
</script>

<style scoped lang="scss">
.profile-hub {
  max-width: 860px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

// Hero
.user-hero {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: var(--shadow);
}

.hero-bg {
  height: 180px;
  background: var(--hero-gradient);
}

.hero-content {
  position: relative;
  display: flex;
  align-items: flex-end;
  gap: 16px;
  padding: 0 24px 24px;
  margin-top: -56px;
}

.avatar-wrap {
  position: relative;
  flex-shrink: 0;

  img {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 4px solid var(--surface);
    object-fit: cover;
    box-shadow: 0 6px 20px rgba(102, 72, 48, 0.15);
  }

  .avatar-badge {
    position: absolute;
    bottom: 4px;
    right: 4px;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: none;
    background: var(--primary);
    color: #fff;
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 6px rgba(241, 124, 83, 0.3);
    transition: background 0.2s;

    &:hover {
      background: var(--primary-strong);
    }
  }
}

.user-detail {
  flex: 1;
  padding-top: 56px;

  h1 {
    margin: 0 0 4px;
    font-size: 22px;
    font-weight: 800;
    color: var(--text);
  }

  .bio {
    margin: 0 0 8px;
    font-size: 14px;
    color: var(--muted);
  }

  .user-meta {
    display: flex;
    gap: 12px;
    font-size: 12px;
    color: var(--muted-soft);
  }
}

.settings-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  color: var(--text-heading-soft);
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(102, 72, 48, 0.06);

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
  }
}

// Stats Row
.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 20px 12px;
  background: var(--surface);
  border-radius: 18px;
  border: 1px solid var(--border-warm);
  box-shadow: var(--shadow);
  text-decoration: none;
  transition: all 0.25s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 30px rgba(102, 72, 48, 0.12);
    border-color: var(--chip-border);
  }

  .stat-icon {
    font-size: 28px;
    margin-bottom: 4px;
  }

  .stat-num {
    font-size: 26px;
    font-weight: 900;
    color: var(--text-heading-soft);
    line-height: 1;
  }

  .stat-label {
    font-size: 12px;
    color: var(--muted);
  }

  .stat-arrow {
    font-size: 18px;
    color: var(--muted-soft);
    margin-top: 4px;
  }
}

// Action Grid
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--surface);
  border-radius: 16px;
  border: 1px solid var(--border-warm);
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 18px rgba(102, 72, 48, 0.08);
    border-color: var(--chip-border);
  }
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;

  &.pets-icon { background: linear-gradient(135deg, #fff1e5, #ffd9a8); }
  &.orders-icon { background: linear-gradient(135deg, #e8f5e9, #c8e6c9); }
  &.bookings-icon { background: linear-gradient(135deg, #e3f2fd, #bbdefb); }
  &.adoption-icon { background: linear-gradient(135deg, #fce4ec, #f8bbd0); }
  &.community-icon { background: linear-gradient(135deg, #fff3e0, #ffe0b2); }
  &.settings-icon { background: linear-gradient(135deg, #f3e5f5, #e1bee7); }
}

.action-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;

  .action-title {
    font-size: 14px;
    font-weight: 700;
    color: var(--text);
  }

  .action-desc {
    font-size: 12px;
    color: var(--muted);
  }
}

.action-arrow {
  font-size: 18px;
  color: var(--muted-soft);
}

// Recent Activity
.recent-section {
  background: var(--surface);
  border-radius: 20px;
  border: 1px solid var(--border-warm);
  box-shadow: var(--shadow);
  overflow: hidden;
}

.section-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-warm);

  h2 {
    margin: 0;
    font-size: 16px;
    font-weight: 800;
    color: var(--text-heading-soft);
  }
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--border-warm);
  transition: background 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--surface-tint);
  }

  .activity-icon {
    font-size: 20px;
    margin-top: 2px;
    flex-shrink: 0;
  }

  .activity-content {
    flex: 1;

    .activity-text {
      margin: 0 0 4px;
      font-size: 14px;
      color: var(--text);

      strong {
        color: var(--text-heading-soft);
      }
    }

    .activity-time {
      font-size: 12px;
      color: var(--muted);
    }
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hero-content {
    flex-wrap: wrap;
    padding: 0 16px 16px;
    gap: 12px;
  }

  .settings-btn {
    position: absolute;
    top: 0;
    right: 16px;
    margin-top: 16px;
  }
}
</style>