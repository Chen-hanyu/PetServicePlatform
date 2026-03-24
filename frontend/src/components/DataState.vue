<template>
  <div class="state">
    <p v-if="loading" class="muted">加载中...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="empty" class="muted">{{ emptyText || "暂无数据" }}</p>
    <slot v-else />
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{ loading?: boolean; error?: string; empty?: boolean; emptyText?: string }>(),
  { loading: false, error: "", empty: false, emptyText: "暂无数据" }
);
</script>

<style scoped>
.error {
  color: var(--danger);
}
</style>
