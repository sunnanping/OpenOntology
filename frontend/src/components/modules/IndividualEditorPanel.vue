<template>
  <div class="individual-editor-panel">
    <div class="placeholder-content">
      <i class="bi bi-people placeholder-icon"></i>
      <h3>Individual Editor</h3>
      <p>Individual editing functionality will be implemented here.</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  projectId: String,
  projectInfo: Object,
  userInfo: Object
})
</script>

<style scoped>
.individual-editor-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
}

.placeholder-content {
  text-align: center;
  color: #666;
}

.placeholder-icon {
  font-size: 64px;
  color: #4a90d9;
  margin-bottom: 16px;
}

.placeholder-content h3 {
  margin-bottom: 8px;
  color: #333;
}

.placeholder-content p {
  font-size: 14px;
}
</style>
