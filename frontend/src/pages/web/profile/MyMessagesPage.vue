<template>
  <section class="messages-hub">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h1 class="page-title">消息通知</h1>
      <button v-if="hasUnread" class="mark-all-btn" @click="markAllRead">全部标为已读</button>
    </div>

    <div class="messages-container">
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
          </svg>
        </div>
        <p class="empty-text">暂无消息通知</p>
      </div>

      <div v-else class="messages-list">
        <div v-for="dateGroup in groupedMessages" :key="dateGroup.date" class="date-group">
          <div class="date-header">{{ dateGroup.date }}</div>
          <div class="messages">
            <article
              v-for="msg in dateGroup.messages"
              :key="msg.id"
              :class="['message-card', { unread: !msg.isRead }]"
              @click="viewMessage(msg)"
            >
              <div class="message-icon" :class="`type-${msg.type}`">
                <svg v-if="msg.type === 'system'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"/>
                  <path d="M12 16v-4M12 8h.01"/>
                </svg>
                <svg v-else-if="msg.type === 'order'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
                </svg>
                <svg v-else-if="msg.type === 'like'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 9V5a3 3 0 00-3-3l-4 9v11h11.28a2 2 0 002-1.7l1.38-9a2 2 0 00-2-2.3zM7 22H4a2 2 0 01-2-2v-7a2 2 0 012-2h3"/>
                </svg>
                <svg v-else-if="msg.type === 'comment'" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                  <path d="M13.73 21a2 2 0 01-3.46 0"/>
                </svg>
              </div>

              <div class="message-content">
                <div class="message-header">
                  <span class="message-title">{{ msg.title }}</span>
                  <span class="message-time">{{ msg.time }}</span>
                </div>
                <p class="message-text">{{ msg.content }}</p>
              </div>

              <div v-if="!msg.isRead" class="unread-dot"></div>

              <button class="delete-btn" @click.stop="deleteMessage(msg)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                </svg>
              </button>
            </article>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, computed } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

interface Message {
  id: number;
  type: string;
  title: string;
  content: string;
  time: string;
  isRead: boolean;
  link?: string;
}

const messages = reactive<Message[]>([
  {
    id: 1,
    type: "system",
    title: "系统通知",
    content: "您的账号已完成实名认证，感谢您对宠物之家的支持！",
    time: "刚刚",
    isRead: false
  },
  {
    id: 2,
    type: "order",
    title: "订单发货提醒",
    content: "您购买的【智能逗猫激光灯】已发货，快递单号：SF1234567890，预计3天内送达。",
    time: "10分钟前",
    isRead: false
  },
  {
    id: 3,
    type: "like",
    title: "收到点赞",
    content: "您的帖子《分享一下糯米刚回家的样子》收到了 15 个赞，继续加油哦~",
    time: "2小时前",
    isRead: false
  },
  {
    id: 4,
    type: "comment",
    title: "新评论",
    content: "用户「萌宠达人」评论了您的帖子：\"糯米好可爱呀！请问是什么品种的猫猫？\"",
    time: "昨天",
    isRead: true
  },
  {
    id: 5,
    type: "system",
    title: "领养申请通过",
    content: "恭喜！您申请领养的【小橘】已通过审核，请于本周六上午10点携带身份证到机构办理手续。",
    time: "昨天",
    isRead: true
  }
]);

const hasUnread = computed(() => messages.some(msg => !msg.isRead));

const groupedMessages = computed(() => {
  const groups: { date: string; messages: Message[] }[] = [];
  const today = "今天";
  const yesterday = "昨天";

  const todayMsgs = messages.filter(m => m.time.includes("刚刚") || m.time.includes("分钟前") || m.time.includes("小时前"));
  const yesterdayMsgs = messages.filter(m => m.time.includes("昨天"));
  const olderMsgs = messages.filter(m => !m.time.includes("刚刚") && !m.time.includes("分钟前") && !m.time.includes("小时前") && !m.time.includes("昨天"));

  if (todayMsgs.length > 0) {
    groups.push({ date: today, messages: todayMsgs });
  }
  if (yesterdayMsgs.length > 0) {
    groups.push({ date: yesterday, messages: yesterdayMsgs });
  }
  if (olderMsgs.length > 0) {
    groups.push({ date: "更早", messages: olderMsgs });
  }

  return groups;
});

const goBack = () => {
  router.back();
};

const markAllRead = () => {
  messages.forEach(msg => {
    msg.isRead = true;
  });
};

const viewMessage = (msg: Message) => {
  msg.isRead = true;
  if (msg.link) {
    router.push(msg.link);
  }
};

const deleteMessage = (msg: Message) => {
  if (confirm("确定要删除该消息吗？")) {
    const index = messages.findIndex(m => m.id === msg.id);
    if (index > -1) {
      messages.splice(index, 1);
    }
  }
};
</script>

<style scoped lang="scss">
.messages-hub {
  min-height: calc(100vh - 80px);
  padding: 24px 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 0 24px;

  .back-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 12px;
    border: none;
    background: var(--surface);
    color: var(--muted);
    font-size: 14px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: var(--surface-muted);
      color: var(--text-heading);
    }
  }

  .page-title {
    flex: 1;
    font-size: 24px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0;
  }

  .mark-all-btn {
    padding: 8px 16px;
    border: 1px solid var(--border-warm);
    background: none;
    color: var(--muted);
    font-size: 13px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--primary);
      color: var(--primary);
    }
  }
}

.messages-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 32px;
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 16px;
    background: var(--surface-muted);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 40px;
      height: 40px;
      color: var(--muted-soft);
    }
  }

  .empty-text {
    font-size: 16px;
    color: var(--muted);
    margin: 0;
  }
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.date-group {
  .date-header {
    font-size: 14px;
    font-weight: 600;
    color: var(--muted);
    margin-bottom: 12px;
    padding-left: 8px;
  }

  .messages {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
}

.message-card {
  position: relative;
  display: flex;
  gap: 16px;
  padding: 20px;
  background: var(--surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  &.unread {
    background: rgba(255, 155, 122, 0.05);
    border-left: 3px solid var(--primary);
  }

  .message-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    svg {
      width: 24px;
      height: 24px;
    }

    &.type-system {
      background: rgba(255, 155, 122, 0.15);
      color: var(--primary);
    }

    &.type-order {
      background: rgba(240, 165, 0, 0.15);
      color: #F0A500;
    }

    &.type-like {
      background: rgba(233, 122, 122, 0.15);
      color: #E97A7A;
    }

    &.type-comment {
      background: rgba(155, 89, 182, 0.15);
      color: #9B59B6;
    }

    &.type-activity {
      background: rgba(91, 185, 140, 0.15);
      color: var(--primary);
    }
  }

  .message-content {
    flex: 1;
    min-width: 0;

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 6px;

      .message-title {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-heading);
      }

      .message-time {
        font-size: 12px;
        color: var(--muted-soft);
        flex-shrink: 0;
      }
    }

    .message-text {
      font-size: 13px;
      color: var(--muted);
      line-height: 1.5;
      margin: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .unread-dot {
    position: absolute;
    top: 20px;
    right: 48px;
    width: 8px;
    height: 8px;
    background: #E97A7A;
    border-radius: 50%;
  }

  .delete-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    width: 28px;
    height: 28px;
    border: none;
    background: none;
    color: var(--muted-soft);
    cursor: pointer;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: var(--surface-muted);
      color: #E97A7A;
    }
  }

  &:hover .delete-btn {
    opacity: 1;
  }
}

@media (max-width: 768px) {
  .messages-container {
    padding: 0 20px;
  }
}
</style>
