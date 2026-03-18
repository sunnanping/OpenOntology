<template>
  <div class="modal fade show" tabindex="-1" style="display: block; background-color: rgba(0, 0, 0, 0.5);">
    <div 
      class="modal-dialog"
      :style="dialogStyle"
    >
      <div 
        class="modal-content"
        :style="contentStyle"
      >
        <div class="modal-header" @mousedown="startDrag">
          <div class="modal-title-wrapper">
            <div class="modal-icon" v-if="icon || showDefaultIcon">
              <img v-if="icon" :src="icon" :alt="'Modal icon'" class="custom-icon" />
              <img v-else src="/src/assets/logo.svg" alt="Default icon" class="default-icon" />
            </div>
            <h5 class="modal-title">{{ title }}</h5>
          </div>
          <button type="button" class="btn-close" @click="closeModal" @mousedown.stop aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <slot></slot>
        </div>
        <div class="modal-footer" v-if="$slots.footer">
          <slot name="footer"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'

export default {
  name: 'DraggableModal',
  props: {
    title: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '500px'
    },
    icon: {
      type: String,
      default: ''
    },
    showDefaultIcon: {
      type: Boolean,
      default: true
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const currentWidth = ref(parseInt(props.width))
    
    // 拖拽相关
    const isDragging = ref(false)
    const startX = ref(0)
    const startY = ref(0)
    const currentX = ref(0)
    const currentY = ref(0)

    const dialogStyle = computed(() => ({
      position: 'absolute',
      left: currentX.value === 0 ? '50%' : `${currentX.value}px`,
      top: currentY.value === 0 ? '50%' : `${currentY.value}px`,
      transform: currentX.value === 0 && currentY.value === 0 ? 'translate(-50%, -50%)' : 'none',
      margin: '0',
      maxWidth: 'none'
    }))

    const contentStyle = computed(() => ({
      width: `${currentWidth.value}px`
    }))

    // 开始拖拽
    const startDrag = (e) => {
      // 如果点击的是关闭按钮，不启动拖拽
      if (e.target.closest('.btn-close')) return
      
      isDragging.value = true
      startX.value = e.clientX
      startY.value = e.clientY
      
      document.addEventListener('mousemove', drag)
      document.addEventListener('mouseup', stopDrag)
    }

    // 拖拽中
    const drag = (e) => {
      if (!isDragging.value) return
      
      // 如果是第一次移动，获取对话框当前的中心位置
      if (currentX.value === 0 && currentY.value === 0) {
        const dialog = document.querySelector('.modal-dialog')
        if (dialog) {
          const rect = dialog.getBoundingClientRect()
          currentX.value = rect.left + rect.width / 2
          currentY.value = rect.top + rect.height / 2
        }
      }
      
      const deltaX = e.clientX - startX.value
      const deltaY = e.clientY - startY.value
      
      currentX.value += deltaX
      currentY.value += deltaY
      
      startX.value = e.clientX
      startY.value = e.clientY
    }

    // 停止拖拽
    const stopDrag = () => {
      isDragging.value = false
      document.removeEventListener('mousemove', drag)
      document.removeEventListener('mouseup', stopDrag)
    }

    const closeModal = () => {
      emit('close')
    }

    // 处理ESC键关闭
    const handleKeydown = (e) => {
      if (e.key === 'Escape') {
        closeModal()
      }
    }

    onMounted(() => {
      document.addEventListener('keydown', handleKeydown)
      // 禁止背景滚动
      document.body.style.overflow = 'hidden'
    })

    onUnmounted(() => {
      document.removeEventListener('keydown', handleKeydown)
      document.body.style.overflow = ''
    })

    return {
      dialogStyle,
      contentStyle,
      startDrag,
      closeModal
    }
  }
}
</script>

<style scoped>
/* Bootstrap Modal 样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1055;
  display: block;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
  overflow-y: auto;
  outline: 0;
}

.modal-dialog {
  position: relative;
  width: auto;
  margin: 0;
  pointer-events: none;
}

.modal-content {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  pointer-events: auto;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid rgba(0, 0, 0, 0.2);
  border-radius: 0.5rem;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
  outline: 0;
}

.modal-header {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1rem;
  border-bottom: 1px solid #dee2e6;
  border-top-left-radius: calc(0.5rem - 1px);
  border-top-right-radius: calc(0.5rem - 1px);
  background-color: #f8f9fa;
  cursor: move;
  user-select: none;
}

.modal-title-wrapper {
  display: flex;
  align-items: center;
  flex: 1;
}

.modal-icon {
  margin-right: 0.5rem;
  display: flex;
  align-items: center;
}

.default-icon,
.custom-icon {
  height: 1.25rem;
  width: auto;
  vertical-align: middle;
  display: inline-block;
}

.modal-title {
  margin-bottom: 0;
  line-height: 1.5;
  font-size: 1.25rem;
  font-weight: 500;
  color: #212529;
}

.btn-close {
  box-sizing: content-box;
  width: 1em;
  height: 1em;
  padding: 0.25em 0.25em;
  color: #000;
  background: transparent url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16' fill='%23000'%3e%3cpath d='M.293.293a1 1 0 011.414 0L8 6.586 14.293.293a1 1 0 111.414 1.414L9.414 8l6.293 6.293a1 1 0 01-1.414 1.414L8 9.414l-6.293 6.293a1 1 0 01-1.414-1.414L6.586 8 .293 1.707a1 1 0 010-1.414z'/%3e%3c/svg%3e") center/1em auto no-repeat;
  border: 0;
  border-radius: 0.25rem;
  opacity: 0.5;
  cursor: pointer;
}

.btn-close:hover {
  opacity: 0.75;
}

.modal-body {
  position: relative;
  flex: 1 1 auto;
  padding: 1rem;
  overflow-y: auto;
}

.modal-footer {
  display: flex;
  flex-wrap: wrap;
  flex-shrink: 0;
  align-items: center;
  justify-content: flex-end;
  padding: 0.75rem;
  border-top: 1px solid #dee2e6;
  border-bottom-right-radius: calc(0.5rem - 1px);
  border-bottom-left-radius: calc(0.5rem - 1px);
  gap: 0.5rem;
}

/* Bootstrap 按钮样式 */
:deep(.btn) {
  display: inline-block;
  font-weight: 400;
  line-height: 1.5;
  text-align: center;
  text-decoration: none;
  vertical-align: middle;
  cursor: pointer;
  user-select: none;
  background-color: transparent;
  border: 1px solid transparent;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  border-radius: 0.25rem;
  transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

:deep(.btn-primary) {
  color: #fff;
  background-color: #0d6efd;
  border-color: #0d6efd;
}

:deep(.btn-primary:hover) {
  background-color: #0b5ed7;
  border-color: #0a58ca;
}

:deep(.btn-secondary) {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}

:deep(.btn-secondary:hover) {
  background-color: #5c636a;
  border-color: #565e64;
}

:deep(.btn-success) {
  color: #fff;
  background-color: #198754;
  border-color: #198754;
}

:deep(.btn-danger) {
  color: #fff;
  background-color: #dc3545;
  border-color: #dc3545;
}

/* Bootstrap 表单样式 */
:deep(.form-group) {
  margin-bottom: 1rem;
}

:deep(.form-group label) {
  display: inline-block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #212529;
}

:deep(.form-control) {
  display: block;
  width: 100%;
  padding: 0.375rem 0.75rem;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  color: #212529;
  background-color: #fff;
  background-clip: padding-box;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
  box-sizing: border-box;
}

:deep(.form-control:focus) {
  color: #212529;
  background-color: #fff;
  border-color: #86b7fe;
  outline: 0;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

:deep(.form-control:disabled) {
  background-color: #e9ecef;
  opacity: 1;
}

:deep(select.form-control) {
  height: 38px;
}

:deep(textarea.form-control) {
  resize: vertical;
}
</style>
