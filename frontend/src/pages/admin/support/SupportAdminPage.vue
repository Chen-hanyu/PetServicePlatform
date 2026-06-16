<template>
  <div class="admin-page">
    <div class="data-card">
      <div class="data-header">
        <div>
          <h3 class="data-title">客服消息</h3>
          <p class="data-subtitle">来自前台在线客服和领养咨询的用户消息</p>
        </div>
        <button class="btn btn-secondary" @click="loadMessages">刷新</button>
      </div>

      <DataState :loading="loading" :error="error" :empty="messages.length === 0">
        <table class="table">
          <thead>
            <tr>
              <th>标题</th>
              <th>咨询内容</th>
              <th>状态</th>
              <th>时间</th>
              <th class="col-ops">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="message in messages" :key="message.id">
              <td>
                <span class="title-text">{{ message.title }}</span>
              </td>
              <td>
                <pre class="message-content">{{ message.content }}</pre>
              </td>
              <td>
                <StatusBadge :variant="message.is_read ? 'success' : 'warning'">
                  {{ message.is_read ? '已处理' : '待处理' }}
                </StatusBadge>
              </td>
              <td>
                <span class="time-text">{{ formatDateTime(message.created_at) }}</span>
              </td>
              <td class="col-ops">
                <button
                  class="btn btn-xs btn-success"
                  :disabled="processingId === message.id"
                  @click="openReplyModal(message)"
                >
                  {{ message.is_read ? '追加回复' : '回复并处理' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <Teleport to="body">
      <div v-if="replyModalVisible" class="modal-overlay" @click.self="closeReplyModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>回复客服咨询</h3>
            <button class="modal-close" type="button" @click="closeReplyModal">&times;</button>
          </div>
          <div v-if="selectedMessage" class="reply-source">
            <p class="reply-label">用户咨询</p>
            <pre>{{ selectedMessage.content }}</pre>
          </div>
          <label class="reply-label" for="replyContent">回复内容</label>
          <textarea
            id="replyContent"
            v-model="replyContent"
            class="reply-input"
            rows="5"
            placeholder="请输入要同步给用户的客服回复..."
          ></textarea>
          <p v-if="replyError" class="reply-error">{{ replyError }}</p>
          <div class="modal-actions">
            <button type="button" class="btn btn-cancel" @click="closeReplyModal">取消</button>
            <button
              type="button"
              class="btn btn-primary"
              :disabled="!replyContent.trim() || Boolean(processingId)"
              @click="submitReply"
            >
              {{ processingId ? '提交中...' : '发送回复' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import StatusBadge from "@/components/StatusBadge.vue";
import { fetchAdminSupportMessages, handleAdminSupportMessage } from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import type { AdminSupportMessage } from "@/types/admin";

const messages = ref<AdminSupportMessage[]>([]);
const loading = ref(false);
const error = ref("");
const processingId = ref<string | number | null>(null);
const replyModalVisible = ref(false);
const selectedMessage = ref<AdminSupportMessage | null>(null);
const replyContent = ref("");
const replyError = ref("");

const formatDateTime = (value?: string) => {
  if (!value) return "-";
  return value.replace("T", " ").slice(0, 16);
};

const loadMessages = async () => {
  loading.value = true;
  error.value = "";
  try {
    const res = await fetchAdminSupportMessages({ page: 1, page_size: 50 });
    messages.value = res.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const openReplyModal = (message: AdminSupportMessage) => {
  selectedMessage.value = message;
  replyContent.value = "";
  replyError.value = "";
  replyModalVisible.value = true;
};

const closeReplyModal = () => {
  if (processingId.value) return;
  replyModalVisible.value = false;
  selectedMessage.value = null;
  replyContent.value = "";
  replyError.value = "";
};

const submitReply = async () => {
  const message = selectedMessage.value;
  const content = replyContent.value.trim();
  if (!message || !content) return;
  processingId.value = message.id;
  error.value = "";
  replyError.value = "";
  try {
    const updated = await handleAdminSupportMessage(message.id, { reply_content: content });
    message.is_read = updated.is_read;
    processingId.value = null;
    closeReplyModal();
  } catch (e) {
    replyError.value = toErrorMessage(e);
  } finally {
    processingId.value = null;
  }
};

onMounted(() => {
  void loadMessages();
});
</script>

<style scoped lang="scss">
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.data-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.06);
  overflow: hidden;
}

.data-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  border-bottom: 1px solid #edf1f1;
}

.data-title {
  margin: 0;
  color: #243532;
  font-size: 20px;
  font-weight: 700;
}

.data-subtitle {
  margin: 6px 0 0;
  color: #7b8a87;
  font-size: 13px;
}

.table {
  width: 100%;
  border-collapse: collapse;

  th,
  td {
    padding: 16px 20px;
    border-bottom: 1px solid #edf1f1;
    text-align: left;
    vertical-align: top;
  }

  th {
    color: #74817f;
    font-size: 13px;
    font-weight: 700;
    background: #f8fbfa;
  }
}

.title-text {
  color: #243532;
  font-weight: 700;
  white-space: nowrap;
}

.message-content {
  max-width: 640px;
  margin: 0;
  color: #42524f;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
}

.time-text {
  color: #7b8a87;
  white-space: nowrap;
}

.col-ops {
  width: 140px;
  white-space: nowrap;
}

.btn {
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 700;

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
}

.btn-secondary {
  padding: 10px 18px;
  border: 1px solid #ffb29d;
  background: #fff8f5;
  color: #5a4038;
}

.btn-xs {
  padding: 8px 12px;
  font-size: 13px;
}

.btn-success {
  background: #e8f7f2;
  color: #249675;
  border: 1px solid #b7e5d8;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(24, 38, 35, 0.42);
}

.modal-content {
  width: min(620px, 100%);
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 24px 70px rgba(31, 45, 61, 0.2);
  padding: 24px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;

  h3 {
    margin: 0;
    color: #243532;
    font-size: 20px;
  }
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: #f2f6f5;
  color: #60716e;
  cursor: pointer;
  font-size: 22px;
}

.reply-source {
  margin-bottom: 16px;
  padding: 14px;
  border-radius: 12px;
  background: #f8fbfa;

  pre {
    margin: 6px 0 0;
    color: #42524f;
    font-family: inherit;
    line-height: 1.7;
    white-space: pre-wrap;
  }
}

.reply-label {
  display: block;
  margin: 0 0 8px;
  color: #60716e;
  font-size: 13px;
  font-weight: 700;
}

.reply-input {
  width: 100%;
  box-sizing: border-box;
  resize: vertical;
  border: 1px solid #d9e5e2;
  border-radius: 12px;
  padding: 12px 14px;
  color: #243532;
  font: inherit;
  outline: none;

  &:focus {
    border-color: #7ecfbc;
    box-shadow: 0 0 0 3px rgba(126, 207, 188, 0.14);
  }
}

.reply-error {
  margin: 10px 0 0;
  color: #d9534f;
  font-size: 13px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 18px;
}

.btn-cancel,
.btn-primary {
  padding: 10px 18px;
}

.btn-cancel {
  background: #f2f6f5;
  color: #60716e;
}

.btn-primary {
  background: #7ecfbc;
  color: #fff;
}
</style>
