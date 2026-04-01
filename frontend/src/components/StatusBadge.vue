<template>
  <span class="status" :class="variantClass"><slot /></span>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{ variant?: "success" | "warning" | "danger" | "info" | "neutral" }>();

const variantClass = computed(() => `is-${props.variant || "neutral"}`);
</script>

<style scoped lang="scss">
.status {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
}

.is-success { background: #e6f7ef; color: #2a865f; }
.is-warning { background: #fff7e3; color: #ad7b15; }
.is-danger { background: #fdeceb; color: #b94d4d; }
.is-info { background: #fdf3e8; color: #9a6540; }
.is-neutral { background: #f5ede6; color: #7d6a5e; }
</style>
