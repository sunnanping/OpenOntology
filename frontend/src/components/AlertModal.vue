<template>
  <div class="modal fade show" tabindex="-1" style="display: block; background-color: rgba(0, 0, 0, 0.5);">
    <div 
      class="modal-dialog" 
      ref="modalDialog"
      :style="dialogStyle"
    >
      <div 
        class="modal-content" 
        ref="modalContent"
        :style="contentStyle"
      >
        <div class="modal-header" @mousedown="startDrag">
          <h5 class="modal-title">
            <span v-if="type === 'success'" class="icon success-icon">✓</span>
            <span v-else-if="type === 'error'" class="icon error-icon">✗</span>
            <span v-else-if="type === 'warning'" class="icon warning-icon">!</span>
            <span v-else-if="type === 'info'" class="icon info-icon">i</span>
            {{ title }}
          </h5>
          <button type="button" class="btn-close" @click="closeModal" aria-label="Close"></button>
        </div>
        <div class="modal-body" @mousedown.stop :style="{ 'display': 'flex', 'align-items': verticalAlign }">
          <div class="alert-message" :style="{ 'white-space': 'pre-line', 'text-align': textAlign, 'width': '100%' }" v-html="processedMessage">
          </div>
        </div>
        <div class="modal-footer" @mousedown.stop>
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="copyMessage"
          >
            {{ t('app.copy') }}
          </button>
          <button 
            type="button" 
            class="btn btn-secondary" 
            @click="captureScreenshot"
          >
            {{ t('app.capture') }}
          </button>
          <button 
            type="button" 
            :class="['btn', buttonClass]" 
            @click="handleConfirm"
          >
            {{ displayConfirmText }}
          </button>
        </div>
        
        <!-- 操作提示 -->
        <div v-if="showTip" class="success-tip">
          {{ tipText }}
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
import { useI18n } from 'vue-i18n'

// 动态引入html2canvas
let html2canvas = null
const loadHtml2Canvas = async () => {
  if (!html2canvas) {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/html2canvas@1.4.1/dist/html2canvas.min.js'
    script.async = true
    await new Promise((resolve) => {
      script.onload = () => {
        html2canvas = window.html2canvas
        resolve()
      }
      document.head.appendChild(script)
    })
  }
  return html2canvas
}

export default {
  name: 'AlertModal',
  props: {
    title: {
      type: String,
      default: '提示'
    },
    message: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: 'info',
      validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
    },
    confirmText: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '400px'
    },
    minWidth: {
      type: Number,
      default: 300
    },
    minHeight: {
      type: Number,
      default: 150
    },
    textAlign: {
      type: String,
      default: 'left',
      validator: (value) => ['left', 'center', 'right'].includes(value)
    },
    verticalAlign: {
      type: String,
      default: 'center',
      validator: (value) => ['top', 'center', 'bottom'].includes(value)
    },
    captureType: {
      type: String,
      default: 'copy',
      validator: (value) => ['copy', 'download'].includes(value)
    },
    tipMaxLength: {
      type: Number,
      default: 10
    },
    tipDuration: {
      type: Number,
      default: 500
    },
    resizable: {
      type: Boolean,
      default: true
    }
  },
  emits: ['close', 'confirm'],
  setup(props, { emit }) {
    const { textAlign, message, captureType, tipMaxLength, tipDuration, verticalAlign, resizable, confirmText } = props
    const modalDialog = ref(null)
    const modalContent = ref(null)
    const { t } = useI18n()
    
    // 提示状态
    const showTip = ref(false)
    const tipText = ref('')
    const isProcessing = ref(false)
    
    // 处理消息内容，将冒号前的内容转换为粗体
    const processedMessage = computed(() => {
      if (!message) return ''
      
      return message.split('\n').map(line => {
        // 查找中文或英文冒号
        const colonIndex = line.search(/[:：]/)
        if (colonIndex !== -1) {
          const beforeColon = line.substring(0, colonIndex + 1)
          const afterColon = line.substring(colonIndex + 1)
          
          // 检查冒号前的内容是否已经包含HTML加粗标签
          const hasBoldTag = /<strong[^>]*>.*<\/strong>/i.test(beforeColon)
          if (!hasBoldTag) {
            return `<strong>${beforeColon}</strong>${afterColon}`
          }
        }
        return line
      }).join('\n')
    })
    
    // 拖拽相关
    const isDragging = ref(false)
    const startX = ref(0)
    const startY = ref(0)
    const currentX = ref(0)
    const currentY = ref(0)
    
    // 缩放相关
    const isResizing = ref(false)
    const resizeDirection = ref('')
    const currentWidth = ref(parseInt(props.width))
    const currentHeight = ref(0)
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
      transform: currentX.value === 0 || currentY.value === 0 ? 'translate(-50%, -50%)' : 'none',
      margin: '0',
      maxWidth: 'none'
    }))

    const contentStyle = computed(() => ({
      width: `${currentWidth.value}px`,
      height: currentHeight.value > 0 ? `${currentHeight.value}px` : 'auto',
      minWidth: `${props.minWidth}px`,
      minHeight: `${props.minHeight}px`
    }))

    const buttonClass = computed(() => {
      switch (props.type) {
        case 'success':
          return 'btn-success'
        case 'error':
          return 'btn-danger'
        case 'warning':
          return 'btn-warning'
        case 'info':
        default:
          return 'btn-primary'
      }
    })

    const displayConfirmText = computed(() => {
      return confirmText || t('app.confirm')
    })

    // 开始拖拽
    const startDrag = (e) => {
      // 只有点击header时才触发拖拽
      if (!e.target.closest('.modal-header')) return
      // 点击关闭按钮时不触发拖拽
      if (e.target.closest('.btn-close')) return
      
      isDragging.value = true
      startX.value = e.clientX
      startY.value = e.clientY
      
      document.addEventListener('mousemove', drag)
      document.addEventListener('mouseup', stopDrag)
    }

    const drag = (e) => {
      if (!isDragging.value) return
      
      // 如果是第一次移动，获取对话框当前的左上角位置
      if (currentX.value === 0 && currentY.value === 0) {
        const rect = modalDialog.value.getBoundingClientRect()
        currentX.value = rect.left
        currentY.value = rect.top
      }
      
      const deltaX = e.clientX - startX.value
      const deltaY = e.clientY - startY.value
      
      currentX.value += deltaX
      currentY.value += deltaY
      
      startX.value = e.clientX
      startY.value = e.clientY
    }

    const stopDrag = () => {
      isDragging.value = false
      document.removeEventListener('mousemove', drag)
      document.removeEventListener('mouseup', stopDrag)
    }

    // 开始缩放
    const startResize = (e, direction) => {
      if (!resizable) return
      
      e.preventDefault()
      e.stopPropagation()
      
      isResizing.value = true
      resizeDirection.value = direction
      startResizeX.value = e.clientX
      startResizeY.value = e.clientY
      
      const rect = modalContent.value.getBoundingClientRect()
      startWidth.value = rect.width
      startHeight.value = rect.height
      
      // 记录初始左上角位置（基于屏幕坐标）
      const dialogRect = modalDialog.value.getBoundingClientRect()
      startCenterX.value = dialogRect.left
      startCenterY.value = dialogRect.top
      
      // 如果是第一次操作，直接使用计算好的绝对位置
      if (currentX.value === 0 && currentY.value === 0) {
        currentX.value = dialogRect.left
        currentY.value = dialogRect.top
      }
      
      document.addEventListener('mousemove', resize)
      document.addEventListener('mouseup', stopResize)
    }

    const resize = (e) => {
      if (!isResizing.value) return
      
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
          // 左侧不动，不需要调整位置
          break
        case 'w':
          // 左侧缩放：左侧宽度变化，右侧不动
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          // 右侧不动，需要调整左侧位置
          newX = startCenterX.value + deltaX
          break
        case 's':
          // 底部缩放：顶部不动，底部高度变化
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 顶部不动，不需要调整位置
          break
        case 'n':
          // 顶部缩放：顶部高度变化，底部高度不变化
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 底部不动，需要调整顶部位置
          newY = startCenterY.value + deltaY
          break
        case 'ne':
          // 右上角缩放：左侧和底部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value + deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 左侧和底部不动，只调整顶部位置
          newY = startCenterY.value + deltaY
          break
        case 'sw':
          // 左下角缩放：右侧和顶部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 右侧和顶部不动，只调整左侧位置
          newX = startCenterX.value + deltaX
          break
        case 'se':
          // 右下角缩放：左侧和顶部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value + deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value + deltaY)
          // 左侧和顶部不动，不需要调整位置
          break
        case 'nw':
          // 左上角缩放：右侧和底部不动，宽度和高度同时变化
          newWidth = Math.max(props.minWidth, startWidth.value - deltaX)
          newHeight = Math.max(props.minHeight, startHeight.value - deltaY)
          // 右侧和底部不动，需要调整左侧和顶部位置
          newX = startCenterX.value + deltaX
          newY = startCenterY.value + deltaY
          break
      }
      
      currentWidth.value = newWidth
      currentHeight.value = newHeight
      currentX.value = newX
      currentY.value = newY
    }

    const stopResize = () => {
      isResizing.value = false
      resizeDirection.value = ''
      document.removeEventListener('mousemove', resize)
      document.removeEventListener('mouseup', stopResize)
    }

    const closeModal = () => {
      emit('close')
    }

    const handleConfirm = () => {
      emit('confirm')
      emit('close')
    }

    // 显示提示
    const showTipMessage = (text, duration = 500) => {
      tipText.value = text
      showTip.value = true
      setTimeout(() => {
        showTip.value = false
      }, duration)
    }

    // 复制消息内容
    const copyMessage = () => {
      isProcessing.value = true
      showTip.value = true
      tipText.value = `${t(`app.${'copy'}`)} now, wait!`
      
      navigator.clipboard.writeText(message).then(() => {
        isProcessing.value = false
        tipText.value = `√ ${t('app.success')} ${t(`app.${'copy'}`)}`
        setTimeout(() => {
          showTip.value = false
        }, tipDuration)
      }).catch(err => {
        isProcessing.value = false
        showTip.value = false
        console.error('Failed to copy message:', err)
      })
    }

    // 截图功能
    const captureScreenshot = () => {
      const modalElement = modalContent.value
      if (modalElement) {
        isProcessing.value = true
        showTip.value = true
        tipText.value = `${t(`app.${'capture'}`)} now, wait!`
        
        // 使用setTimeout确保提示先显示
        setTimeout(async () => {
          try {
            const html2canvas = await loadHtml2Canvas()
            const canvas = await html2canvas(modalElement, {
              ignoreElements: (element) => {
                return element.classList.contains('success-tip')
              }
            })
            
            if (captureType === 'copy') {
              // 复制到内存
              canvas.toBlob(async (blob) => {
                try {
                  await navigator.clipboard.write([
                    new ClipboardItem({
                      'image/png': blob
                    })
                  ])
                  isProcessing.value = false
                  tipText.value = `√ ${t('app.success')} ${t(`app.${'capture'}`)}`
                  setTimeout(() => {
                    showTip.value = false
                  }, tipDuration)
                } catch (clipboardErr) {
                  isProcessing.value = false
                  showTip.value = false
                  console.error('Failed to copy screenshot to clipboard:', clipboardErr)
                }
              })
            } else {
              // 下载截图文件
              const link = document.createElement('a')
              link.download = `alert-screenshot-${Date.now()}.png`
              link.href = canvas.toDataURL('image/png')
              link.click()
              isProcessing.value = false
              tipText.value = `√ ${t('app.success')} ${t(`app.${'capture'}`)}`
              setTimeout(() => {
                showTip.value = false
              }, tipDuration)
            }
          } catch (err) {
            isProcessing.value = false
            showTip.value = false
            console.error('Failed to capture screenshot:', err)
          }
        }, 50)
      }
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
      modalDialog,
      modalContent,
      dialogStyle,
      contentStyle,
      buttonClass,
      displayConfirmText,
      textAlign,
      verticalAlign,
      captureType,
      tipMaxLength,
      tipDuration,
      resizable,
      showTip,
      tipText,
      isProcessing,
      processedMessage,
      startDrag,
      startResize,
      closeModal,
      handleConfirm,
      copyMessage,
      captureScreenshot,
      t
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

.modal-title {
  margin-bottom: 0;
  line-height: 1.5;
  font-size: 1.25rem;
  font-weight: 500;
  color: #212529;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.success-icon {
  background-color: #198754;
}

.error-icon {
  background-color: #dc3545;
}

.warning-icon {
  background-color: #ffc107;
  color: #212529;
}

.info-icon {
  background-color: #0d6efd;
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
  padding: 1.5rem;
  overflow-y: auto;
  min-height: 100px;
}

.alert-message {
  font-size: 16px;
  line-height: 1.5;
  color: #212529;
}

.modal-footer {
  display: flex;
  flex-wrap: wrap;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
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

/* 成功提示样式 */
.success-tip {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  background-color: #0d6efd;
  color: white;
  padding: 8px 20px;
  border-radius: 0 0 4px 4px;
  font-size: 16px;
  font-weight: 600;
  z-index: 1000;
  min-width: 120px;
  text-align: center;
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
  padding: 0.375rem 1.5rem;
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

:deep(.btn-success:hover) {
  background-color: #157347;
  border-color: #146c43;
}

:deep(.btn-danger) {
  color: #fff;
  background-color: #dc3545;
  border-color: #dc3545;
}

:deep(.btn-danger:hover) {
  background-color: #bb2d3b;
  border-color: #b02a37;
}

:deep(.btn-warning) {
  color: #212529;
  background-color: #ffc107;
  border-color: #ffc107;
}

:deep(.btn-warning:hover) {
  background-color: #ffca2c;
  border-color: #ffc720;
}
</style>