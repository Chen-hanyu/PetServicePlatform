<template>
  <section class="settings-page">
    <div class="page-hero card">
      <h1>账户设置</h1>
      <p>管理你的个人信息与偏好</p>
    </div>

    <!-- Profile Info Card -->
    <div class="settings-card card">
      <div class="card-header">
        <h3>👤 个人信息</h3>
      </div>
      <div class="profile-info">
        <div class="avatar-upload">
          <div class="avatar-preview">
            <img :src="userInfo.avatar || defaultAvatar" alt="avatar" />
          </div>
          <div class="avatar-actions">
            <button class="btn btn-secondary" @click="triggerAvatarInput">更换头像</button>
            <input ref="avatarInput" type="file" accept="image/*" class="hidden-input" @change="onAvatarChange" />
            <p class="avatar-tip">支持 JPG、PNG，建议尺寸 200×200</p>
          </div>
        </div>
        <div class="info-fields">
          <div class="field-group">
            <label>昵称</label>
            <div class="field-value" v-if="!editingNickname">
              <span>{{ userInfo.nickname }}</span>
              <button class="edit-link" @click="editingNickname = true">编辑</button>
            </div>
            <div class="field-edit" v-else>
              <input v-model="editNickname" class="input" />
              <button class="btn btn-primary" @click="saveNickname">保存</button>
              <button class="btn btn-secondary" @click="editingNickname = false">取消</button>
            </div>
          </div>
          <div class="field-group">
            <label>个人简介</label>
            <div class="field-value" v-if="!editingBio">
              <span>{{ userInfo.bio || "还没有个人简介" }}</span>
              <button class="edit-link" @click="editingBio = true">编辑</button>
            </div>
            <div class="field-edit" v-else>
              <textarea v-model="editBio" class="input" rows="3"></textarea>
              <button class="btn btn-primary" @click="saveBio">保存</button>
              <button class="btn btn-secondary" @click="editingBio = false">取消</button>
            </div>
          </div>
          <div class="field-group">
            <label>绑定手机</label>
            <div class="field-value">
              <span>{{ userInfo.phone }}</span>
              <span class="verified-badge">已认证 ✓</span>
            </div>
          </div>
          <div class="field-group">
            <label>绑定邮箱</label>
            <div class="field-value">
              <span>{{ userInfo.email }}</span>
              <span class="verified-badge">已认证 ✓</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Security Card -->
    <div class="settings-card card">
      <div class="card-header">
        <h3>🔐 账户安全</h3>
      </div>
      <div class="setting-list">
        <div class="setting-row" @click="showPasswordForm = true">
          <div class="setting-info">
            <span class="setting-icon">🔑</span>
            <div>
              <span class="setting-title">修改密码</span>
              <span class="setting-desc">定期更换密码保护账户安全</span>
            </div>
          </div>
          <span class="arrow">›</span>
        </div>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-icon">📱</span>
            <div>
              <span class="setting-title">绑定手机</span>
              <span class="setting-desc">用于快捷登录与接收通知</span>
            </div>
          </div>
          <span class="verified-tag">已绑定 ✓</span>
        </div>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-icon">✉️</span>
            <div>
              <span class="setting-title">绑定邮箱</span>
              <span class="setting-desc">用于找回密码与重要通知</span>
            </div>
          </div>
          <span class="verified-tag">已绑定 ✓</span>
        </div>
      </div>
    </div>

    <!-- Preferences Card -->
    <div class="settings-card card">
      <div class="card-header">
        <h3>⚙️ 偏好设置</h3>
      </div>
      <div class="setting-list">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-icon">🔔</span>
            <div>
              <span class="setting-title">消息通知</span>
              <span class="setting-desc">订单状态、服务预约等通知</span>
            </div>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" v-model="preferences.notifications" />
            <span class="toggle-slider"></span>
          </label>
        </div>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-icon">📧</span>
            <div>
              <span class="setting-title">营销通知</span>
              <span class="setting-desc">活动优惠、新品推荐等信息</span>
            </div>
          </div>
          <label class="toggle-switch">
            <input type="checkbox" v-model="preferences.marketing" />
            <span class="toggle-slider"></span>
          </label>
        </div>
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-icon">🌐</span>
            <div>
              <span class="setting-title">显示语言</span>
              <span class="setting-desc">选择界面显示语言</span>
            </div>
          </div>
          <span class="setting-value">简体中文</span>
        </div>
      </div>
    </div>

    <!-- Danger Zone -->
    <div class="settings-card danger-card card">
      <div class="card-header">
        <h3>⚠️ 危险区域</h3>
      </div>
      <div class="setting-list">
        <div class="setting-row logout-row" @click="handleLogout">
          <div class="setting-info">
            <span class="setting-icon">🚪</span>
            <div>
              <span class="setting-title">退出登录</span>
              <span class="setting-desc">当前设备将退出登录状态</span>
            </div>
          </div>
          <span class="arrow">›</span>
        </div>
        <div class="setting-row danger-row" @click="handleDeleteAccount">
          <div class="setting-info">
            <span class="setting-icon">🗑️</span>
            <div>
              <span class="setting-title">删除账户</span>
              <span class="setting-desc">永久删除账户及所有相关数据</span>
            </div>
          </div>
          <span class="arrow">›</span>
        </div>
      </div>
    </div>

    <!-- Change Password Modal -->
    <div v-if="showPasswordForm" class="modal-overlay" @click.self="showPasswordForm = false">
      <div class="modal-content card">
        <button class="close-btn" @click="showPasswordForm = false">×</button>
        <h3>修改密码</h3>
        <div class="form-group">
          <label>当前密码</label>
          <input v-model="passwordForm.current" type="password" class="input" placeholder="请输入当前密码" />
        </div>
        <div class="form-group">
          <label>新密码</label>
          <input v-model="passwordForm.new" type="password" class="input" placeholder="请输入新密码（6位以上）" />
        </div>
        <div class="form-group">
          <label>确认新密码</label>
          <input v-model="passwordForm.confirm" type="password" class="input" placeholder="请再次输入新密码" />
        </div>
        <div class="form-actions">
          <button class="btn btn-secondary" @click="showPasswordForm = false">取消</button>
          <button class="btn btn-primary" @click="submitPassword">确认修改</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const defaultAvatar = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=200&q=80";

const router = useRouter();
const auth = useAuthStore();

const avatarInput = ref<HTMLInputElement | null>(null);
const editingNickname = ref(false);
const editingBio = ref(false);
const editNickname = ref("");
const editBio = ref("");
const showPasswordForm = ref(false);

const userInfo = reactive({
  nickname: auth.user?.nickname || "萌宠达人",
  bio: "热爱小动物，养宠5年经验",
  phone: "138****8888",
  email: "pet@lover.com",
  avatar: auth.user?.avatar_url || defaultAvatar
});

const preferences = reactive({
  notifications: true,
  marketing: false
});

const passwordForm = reactive({
  current: "",
  new: "",
  confirm: ""
});

const triggerAvatarInput = () => {
  avatarInput.value?.click();
};

const onAvatarChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) {
    const url = URL.createObjectURL(file);
    userInfo.avatar = url;
  }
};

const saveNickname = () => {
  if (editNickname.value.trim()) {
    userInfo.nickname = editNickname.value.trim();
  }
  editingNickname.value = false;
};

const saveBio = () => {
  userInfo.bio = editBio.value.trim();
  editingBio.value = false;
};

const submitPassword = () => {
  if (!passwordForm.current) {
    alert("请输入当前密码");
    return;
  }
  if (passwordForm.new.length < 6) {
    alert("新密码至少6位");
    return;
  }
  if (passwordForm.new !== passwordForm.confirm) {
    alert("两次输入的密码不一致");
    return;
  }
  alert("密码修改成功！");
  showPasswordForm.value = false;
  passwordForm.current = "";
  passwordForm.new = "";
  passwordForm.confirm = "";
};

const handleLogout = () => {
  if (confirm("确定要退出登录吗？")) {
    auth.logout();
    router.push("/login");
  }
};

const handleDeleteAccount = () => {
  if (confirm("确定要永久删除账户吗？此操作不可恢复！")) {
    if (confirm("再次确认：删除账户将清除所有数据，确定继续吗？")) {
      alert("账户删除功能暂未开放，请联系客服处理。");
    }
  }
};
</script>

<style scoped lang="scss">
.settings-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 760px;
  margin: 0 auto;
}

.settings-card {
  border-radius: 20px;
  overflow: hidden;
}

.card-header {
  padding: 16px 20px;
  background: var(--surface-tint);
  border-bottom: 1px solid var(--border-warm);

  h3 {
    margin: 0;
    font-size: 16px;
    font-weight: 800;
    color: var(--text-heading-soft);
  }
}

.profile-info {
  padding: 20px;
}

.avatar-upload {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-warm);
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--border-warm);
  box-shadow: 0 4px 12px rgba(102, 72, 48, 0.1);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .hidden-input {
    display: none;
  }

  .avatar-tip {
    margin: 0;
    font-size: 12px;
    color: var(--muted);
  }
}

.info-fields {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;

  > label {
    font-size: 13px;
    font-weight: 600;
    color: var(--muted);
  }
}

.field-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  background: var(--surface-tint);
  border-radius: 12px;
  border: 1px solid var(--border-warm);

  span {
    font-size: 14px;
    color: var(--text);
  }

  .edit-link {
    background: none;
    border: none;
    color: var(--primary-strong);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;

    &:hover { text-decoration: underline; }
  }

  .verified-badge {
    font-size: 12px;
    color: var(--success);
    font-weight: 600;
  }
}

.field-edit {
  display: flex;
  gap: 8px;
  align-items: flex-start;

  .input {
    flex: 1;
  }
}

.setting-list {
  display: flex;
  flex-direction: column;
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-warm);
  cursor: pointer;
  transition: background 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: var(--surface-tint);
  }
}

.setting-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .setting-icon {
    font-size: 22px;
  }

  div {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .setting-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--text);
  }

  .setting-desc {
    font-size: 12px;
    color: var(--muted);
  }
}

.arrow {
  font-size: 20px;
  color: var(--muted-soft);
}

.verified-tag {
  font-size: 12px;
  color: var(--success);
  font-weight: 600;
  background: var(--status-success-bg);
  padding: 3px 10px;
  border-radius: 10px;
}

.setting-value {
  font-size: 14px;
  color: var(--muted);
}

// Toggle Switch
.toggle-switch {
  position: relative;
  width: 48px;
  height: 26px;
  cursor: pointer;

  input {
    opacity: 0;
    width: 0;
    height: 0;

    &:checked + .toggle-slider {
      background: var(--primary-strong);

      &::before {
        transform: translateX(22px);
      }
    }
  }

  .toggle-slider {
    position: absolute;
    inset: 0;
    background: var(--surface-muted);
    border-radius: 999px;
    transition: all 0.25s;

    &::before {
      content: "";
      position: absolute;
      left: 3px;
      top: 3px;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: #fff;
      box-shadow: 0 1px 4px rgba(0,0,0,0.15);
      transition: transform 0.25s;
    }
  }
}

// Danger Zone
.danger-card {
  border: 1px solid #f8d0d0;

  .card-header {
    background: #fff5f5;
    h3 { color: var(--danger); }
  }

  .logout-row:hover {
    background: #fff9f9;
  }

  .logout-row .setting-title {
    color: var(--warning);
  }

  .danger-row:hover {
    background: #fff5f5;
  }

  .danger-row .setting-title {
    color: var(--danger);
  }
}

// Modal
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--overlay-scrim);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal-content {
  width: 100%;
  max-width: 420px;
  border-radius: 20px;
  position: relative;
  padding: 28px;

  h3 {
    margin: 0 0 20px;
    font-size: 18px;
    font-weight: 800;
    color: var(--text-heading-soft);
  }
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: var(--surface-muted);
  color: var(--muted);
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover { background: var(--surface-muted-hover); }
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;

  label {
    font-size: 13px;
    font-weight: 600;
    color: var(--muted);
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .avatar-upload {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>