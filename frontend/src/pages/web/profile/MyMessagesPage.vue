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
      <div v-if="loading" class="empty-state">
        <p class="empty-text">消息加载中...</p>
      </div>
      <div v-else-if="error" class="empty-state">
        <p class="error-text">{{ error }}</p>
        <button class="mark-all-btn" @click="loadMessages">重试</button>
      </div>
      <div v-else-if="messages.length === 0" class="empty-state">
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
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  deleteMessage as deleteMessageApi,
  fetchMessages,
  markAllMessagesRead,
  markMessageRead
} from "@/api/modules/messages";
import { toErrorMessage } from "@/api/http";

const router = useRouter();

const loading = ref(true);
const error = ref("");

interface Message {
  id: string | number;
  type: string;
  title: string;
  content: string;
  time: string;
  isRead: boolean;
  link?: string;
}

const messages = ref<Message[]>([]);

const normalizeType = (type?: string) => String(type || "system").toLowerCase();

const formatRelativeTime = (value?: string) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  const diff = Date.now() - date.getTime();
  if (diff < 60000) return "刚刚";
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 172800000) return "昨天";
  return date.toLocaleDateString("zh-CN", { month: "2-digit", day: "2-digit" });
};

const loadMessages = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchMessages({ page: 1, page_size: 50 });
    messages.value = (data.list ?? []).map((item: any) => ({
      id: item.id,
      type: normalizeType(item.type),
      title: item.title,
      content: item.content,
      time: formatRelativeTime(item.created_at),
      isRead: Boolean(item.is_read ?? item.isRead),
      link: item.link
    }));
  } catch (e) {
    error.value = toErrorMessage(e);
    messages.value = [];
  } finally {
    loading.value = false;
  }
};

const hasUnread = computed(() => messages.value.some(msg => !msg.isRead));

const groupedMessages = computed(() => {
  const groups: { date: string; messages: Message[] }[] = [];
  const today = "今天";
  const yesterday = "昨天";

  const todayMsgs = messages.value.filter(m => m.time.includes("刚刚") || m.time.includes("分钟前") || m.time.includes("小时前"));
  const yesterdayMsgs = messages.value.filter(m => m.time.includes("昨天"));
  const olderMsgs = messages.value.filter(m => !m.time.includes("刚刚") && !m.time.includes("分钟前") && !m.time.includes("小时前") && !m.time.includes("昨天"));

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

const markAllRead = async () => {
  try {
    await markAllMessagesRead();
    messages.value.forEach(msg => {
      msg.isRead = true;
    });
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const viewMessage = async (msg: Message) => {
  if (!msg.isRead) {
    try {
      await markMessageRead(msg.id);
      msg.isRead = true;
    } catch (e) {
      error.value = toErrorMessage(e);
    }
  }
  if (msg.link) {
    router.push(msg.link);
  }
};

const deleteMessage = async (msg: Message) => {
  if (confirm("确定要删除该消息吗？")) {
    try {
      await deleteMessageApi(msg.id);
      const index = messages.value.findIndex(m => m.id === msg.id);
      if (index > -1) {
        messages.value.splice(index, 1);
      }
    } catch (e) {
      error.value = toErrorMessage(e);
    }
  }
};

onMounted(loadMessages);
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

  .error-text {
    font-size: 16px;
    color: #E97A7A;
    margin: 0 0 16px;
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
