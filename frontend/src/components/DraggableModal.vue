<template>
  <div class="modal fade show" tabindex="-1" style="display: block; background-color: rgba(0, 0, 0, 0.5);">
    <div 
      class="modal-dialog"
      :style="dialogStyle"
    >
      <div 
        class="modal-content"
        ref="modalContent"
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
        
        <!-- 拉伸缩放手柄 -->
        <div v-if="resizable" class="resize-handle resize-e" @mousedown="startResize($event, 'e')"></div>
        <div v-if="resizable" class="resize-handle resize-w" @mousedown="startResize($event, 'w')"></div>
        <div v-if="resizable" class="resize-handle resize-n" @mousedown="startResize($event, 'n')"></div>
        <div v-if="resizable" class="resize-handle resize-s" @mousedown="startResize($event, 's')"></div>
        <div v-if="resizable" class="resize-handle resize-ne" @mousedown="startResize($event, 'ne')"></div>
        <div v-if="resizable" class="resize-handle resize-nw" @mousedown="startResize($event, 'nw')"></div>
        <div v-if="resizable" class="resize-handle resize-se" @mousedown="startResize($event, 'se')"></div>
        <div v-if="resizable" class="resize-handle resize-sw" @mousedown="startResize($event, 'sw')"></div>
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
    },
    resizable: {
      type: Boolean,
      default: true
    },
    minWidth: {
      type: Number,
      default: 300
    },
    minHeight: {
      type: Number,
      default: 200
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const modalContent = ref(null)
    const currentWidth = ref(parseInt(props.width))
    const currentHeight = ref(0)
    
    // 拖拽相关
    const isDragging = ref(false)
    const startX = ref(0)
    const startY = ref(0)
    const currentX = ref(0)
    const currentY = ref(0)
    
    // 缩放相关
    const isResizing = ref(false)
    const resizeDirection = ref('')
    const startWidth = ref(0)
    const startHeight = ref(0)
    const startResizeX = ref(0)
    const startResizeY = ref(0)
    const startCenterX = ref(0)
    const startCenterY = ref(0)

    const dialogStyle = computed(() => ({
      position: 'absolute',
      left: currentX.value === 0 ? '50%' : `${currentX.value}px`,
      top: currentY.value === 0 ? '50%' : `${currentY.value}px`,
      transform: 'translate(-50%, -50%)',
      margin: '0',
      maxWidth: 'none'
    }))

    const contentStyle = computed(() => ({
      width: `${currentWidth.value}px`,
      height: currentHeight.value > 0 ? `${currentHeight.value}px` : 'auto',
      minWidth: `${props.minWidth}px`,
      minHeight: `${props.minHeight}px`
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

    // 开始缩放
    const startResize = (e, direction) => {
      if (!props.resizable) return
      
      e.preventDefault()
      e.stopPropagation()
      
      isResizing.value = true
      resizeDirection.value = direction
      startResizeX.value = e.clientX
      startResizeY.value = e.clientY
      
      const rect = modalContent.value.getBoundingClientRect()
      startWidth.value = rect.width
      startHeight.value = rect.height
      
      const dialogRect = document.querySelector('.modal-dialog').getBoundingClientRect()
      startCenterX.value = dialogRect.left + dialogRect.width / 2
      startCenterY.value = dialogRect.top + dialogRect.height / 2
      
      if (currentX.value === 0 && currentY.value === 0) {
        currentX.value = startCenterX.value
        currentY.value = startCenterY.value
      }
      
      document.addEventListener('mousemove', resize)
      document.addEventListener('mouseup', stopResize)
    }

    // 缩放中
    const resize = (e) => {
      if (!isResizing.value) return
      
      // 如果是第一次操作，获取对话框当前的中心位置
      if (currentX.value === 0 && currentY.value === 0) {
        const dialogRect = document.querySelector('.modal-dialog').getBoundingClientRect()
        currentX.value = dialogRect.left + dialogRect.width / 2
        currentY.value = dialogRect.top + dialogRect.height / 2
        // 重新计算startResizeX和startResizeY，确保第一次缩放的delta为0
        startResizeX.value = e.clientX
        startResizeY.value = e.clientY
      }
      
      const deltaX = e.clientX - startResizeX.value
      const deltaY = e.clientY - startResizeY.value
      
      let newWidth = startWidth.value
      let newHeight = startHeight.value
      let newX = currentX.value
      let newY = currentY.value
      
      // 根据方向调整大小
      switch (resizeDirection.value) {
        case 'e':
          // 右侧缩放：左侧不动，右侧宽度变化
          newWidth = Math.max(props.minWidth, startWidth.value + deltaX)
          // 左侧不动，需要调整中心位置
          newX = startCenterX.value + deltaX / 2
          break
        case 'w':
          // 左侧缩放：左侧宽度变化，右侧不动
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          // 右侧不动，需要调整中心位置，确保右侧边缘位置不变
          newX = startCenterX.value + deltaX / 2
          break
        case 's':
          // 底部缩放：顶部不动，底部高度变化
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 顶部不动，需要调整中心位置
          newY = startCenterY.value + deltaY / 2
          break
        case 'n':
          // 顶部缩放：顶部高度变化，底部高度不变化
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 底部不动，需要调整中心位置，确保底部边缘位置不变
          newY = startCenterY.value + deltaY / 2
          break
        case 'ne':
          // 右上角缩放：左侧和底部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value + deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 左侧和底部不动，需要调整中心位置
          newX = startCenterX.value + deltaX / 2
          newY = startCenterY.value + deltaY / 2
          break
        case 'sw':
          // 左下角缩放：右侧和顶部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 右侧和顶部不动，需要调整中心位置
          newX = startCenterX.value + deltaX / 2
          newY = startCenterY.value + deltaY / 2
          break
        case 'se':
          // 右下角缩放：左侧和顶部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value + deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 左侧和顶部不动，需要调整中心位置
          newX = startCenterX.value + deltaX / 2
          newY = startCenterY.value + deltaY / 2
          break
        case 'nw':
          // 左上角缩放：右侧和底部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 右侧和底部不动，需要调整中心位置
          newX = startCenterX.value + deltaX / 2
          newY = startCenterY.value + deltaY / 2
          break
      }
      
      currentWidth.value = newWidth
      currentHeight.value = newHeight
      currentX.value = newX
      currentY.value = newY
    }

    // 停止缩放
    const stopResize = () => {
      isResizing.value = false
      resizeDirection.value = ''
      document.removeEventListener('mousemove', resize)
      document.removeEventListener('mouseup', stopResize)
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
      // 初始化高度
      if (modalContent.value) {
        currentHeight.value = modalContent.value.offsetHeight
      }
    })

    onUnmounted(() => {
      document.removeEventListener('keydown', handleKeydown)
      document.body.style.overflow = ''
    })

    return {
      modalContent,
      dialogStyle,
      contentStyle,
      resizable: props.resizable,
      startDrag,
      startResize,
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

/* 拉伸缩放手柄样式 */
.resize-handle {
  position: absolute;
  z-index: 10;
}

.resize-e {
  top: 0;
  right: -5px;
  width: 10px;
  height: 100%;
  cursor: e-resize;
}

.resize-w {
  top: 0;
  left: -5px;
  width: 10px;
  height: 100%;
  cursor: w-resize;
}

.resize-n {
  top: -5px;
  left: 0;
  width: 100%;
  height: 10px;
  cursor: n-resize;
}

.resize-s {
  bottom: -5px;
  left: 0;
  width: 100%;
  height: 10px;
  cursor: s-resize;
}

.resize-ne {
  top: -5px;
  right: -5px;
  width: 15px;
  height: 15px;
  cursor: ne-resize;
}

.resize-nw {
  top: -5px;
  left: -5px;
  width: 15px;
  height: 15px;
  cursor: nw-resize;
}

.resize-se {
  bottom: -5px;
  right: -5px;
  width: 15px;
  height: 15px;
  cursor: se-resize;
}

.resize-sw {
  bottom: -5px;
  left: -5px;
  width: 15px;
  height: 15px;
  cursor: sw-resize;
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
