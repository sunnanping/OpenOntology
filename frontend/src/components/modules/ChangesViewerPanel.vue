<template>
  <div class="changes-viewer-panel">
    <div class="placeholder-content">
      <i class="bi bi-clock-history placeholder-icon"></i>
      <h3>Changes by Entity</h3>
      <p>Entity changes history will be displayed here.</p>
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
.changes-viewer-panel {
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
