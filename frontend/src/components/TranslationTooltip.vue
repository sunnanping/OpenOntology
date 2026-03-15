<template>
  <div class="translation-tooltip-container">
    <span
      :class="['translation-text', { 'unconfirmed': !isConfirmed }]"
      @mouseenter="showTooltip"
      @mouseleave="hideTooltip"
    >
      {{ displayText }}
    </span>

    <div
      v-if="tooltipVisible"
      class="translation-tooltip"
      :style="{ top: tooltipPosition.top, left: tooltipPosition.left }"
    >
      <div class="tooltip-content">
        <div class="translation-info">
          <div class="info-row">
            <span class="label">{{ $t('translation.proposedBy') }}:</span>
            <span class="value">{{ translationInfo.lastProposedBy }}</span>
          </div>
          <div class="info-row">
            <span class="label">{{ $t('translation.proposedAt') }}:</span>
            <span class="value">{{ formatDate(translationInfo.lastProposedAt) }}</span>
          </div>
          <div v-if="translationInfo.lastConfirmedBy" class="info-row">
            <span class="label">{{ $t('translation.confirmedBy') }}:</span>
            <span class="value">{{ translationInfo.lastConfirmedBy }}</span>
          </div>
          <div v-if="translationInfo.lastConfirmedAt" class="info-row">
            <span class="label">{{ $t('translation.confirmedAt') }}:</span>
            <span class="value">{{ formatDate(translationInfo.lastConfirmedAt) }}</span>
          </div>
          <div class="info-row">
            <span class="label">{{ $t('translation.source') }}:</span>
            <span class="value">{{ translationInfo.source }}</span>
          </div>
        </div>

        <div class="tooltip-actions">
          <button
            v-if="!isConfirmed"
            @click="handleConfirm"
            class="btn btn-sm btn-primary"
          >
            {{ $t('translation.confirm') }}
          </button>
          <button
            v-if="!isConfirmed"
            @click="handleEdit"
            class="btn btn-sm btn-secondary"
          >
            {{ $t('translation.edit') }}
          </button>
          <button
            v-if="isAdmin && !isConfirmed"
            @click="handleReject"
            class="btn btn-sm btn-danger"
          >
            {{ $t('translation.reject') }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showEditDialog" class="edit-dialog-overlay" @click="closeEditDialog">
      <div class="edit-dialog" @click.stop>
        <h4>{{ $t('translation.editTranslation') }}</h4>
        <div class="form-group">
          <label>{{ $t('translation.newValue') }}</label>
          <textarea
            v-model="editValue"
            class="form-control"
            rows="3"
          ></textarea>
        </div>
        <div class="dialog-actions">
          <button @click="saveEdit" class="btn btn-primary">
            {{ $t('common.save') }}
          </button>
          <button @click="closeEditDialog" class="btn btn-secondary">
            {{ $t('common.cancel') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'TranslationTooltip',
  props: {
    text: {
      type: String,
      required: true
    },
    entityRef: {
      type: String,
      required: true
    },
    entityType: {
      type: String,
      required: true
    },
    langTag: {
      type: String,
      required: true
    },
    isConfirmed: {
      type: Boolean,
      default: false
    },
    translationInfo: {
      type: Object,
      default: () => ({})
    },
    isAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      tooltipVisible: false,
      tooltipPosition: { top: '0px', left: '0px' },
      showEditDialog: false,
      editValue: ''
    };
  },
  computed: {
    displayText() {
      return this.text || this.$t('translation.noTranslation');
    }
  },
  methods: {
    showTooltip(event) {
      if (this.isConfirmed) return;
      
      const rect = event.target.getBoundingClientRect();
      this.tooltipPosition = {
        top: `${rect.bottom + 5}px`,
        left: `${rect.left}px`
      };
      this.tooltipVisible = true;
    },
    hideTooltip() {
      this.tooltipVisible = false;
    },
    formatDate(dateString) {
      if (!dateString) return '-';
      const date = new Date(dateString);
      return date.toLocaleString();
    },
    async handleConfirm() {
      try {
        const response = await axios.post(
          `http://localhost:8086/api/i18n/confirm-translation/${this.entityType}/${this.entityRef}/${this.langTag}`,
          {},
          {
            headers: {
              'X-User-Name': this.$store.state.user.username
            }
          }
        );
        
        this.$emit('confirmed', response.data);
        this.hideTooltip();
        this.$message.success(this.$t('translation.confirmSuccess'));
      } catch (error) {
        console.error('Error confirming translation:', error);
        this.$message.error(this.$t('translation.confirmError'));
      }
    },
    handleEdit() {
      this.editValue = this.text;
      this.showEditDialog = true;
      this.hideTooltip();
    },
    async saveEdit() {
      try {
        await axios.post(
          `http://localhost:8086/api/i18n/translation/${this.entityType}/${this.entityRef}/${this.langTag}`,
          {
            value: this.editValue
          },
          {
            headers: {
              'X-User-Name': this.$store.state.user.username
            }
          }
        );
        
        this.$emit('updated', this.editValue);
        this.closeEditDialog();
        this.$message.success(this.$t('translation.updateSuccess'));
      } catch (error) {
        console.error('Error updating translation:', error);
        this.$message.error(this.$t('translation.updateError'));
      }
    },
    async handleReject() {
      const comment = prompt(this.$t('translation.rejectReason'));
      if (comment === null) return;
      
      try {
        await axios.post(
          `http://localhost:8086/api/i18n/reject-translation/${this.entityType}/${this.entityRef}/${this.langTag}`,
          { comment },
          {
            headers: {
              'X-User-Name': this.$store.state.user.username
            }
          }
        );
        
        this.$emit('rejected');
        this.hideTooltip();
        this.$message.success(this.$t('translation.rejectSuccess'));
      } catch (error) {
        console.error('Error rejecting translation:', error);
        this.$message.error(this.$t('translation.rejectError'));
      }
    },
    closeEditDialog() {
      this.showEditDialog = false;
      this.editValue = '';
    }
  }
};
</script>

<style scoped>
.translation-tooltip-container {
  display: inline-block;
  position: relative;
}

.translation-text {
  cursor: default;
  transition: color 0.3s;
}

.translation-text.unconfirmed {
  color: #999;
  cursor: pointer;
  text-decoration: underline;
  text-decoration-style: dotted;
}

.translation-text.unconfirmed:hover {
  color: #666;
}

.translation-tooltip {
  position: fixed;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  padding: 12px;
  z-index: 1000;
  min-width: 250px;
  max-width: 400px;
}

.tooltip-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.translation-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.info-row .label {
  font-weight: 600;
  color: #666;
}

.info-row .value {
  color: #333;
}

.tooltip-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.btn {
  padding: 4px 12px;
  font-size: 12px;
  border-radius: 3px;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-sm {
  padding: 3px 8px;
  font-size: 11px;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
}

.btn-primary:hover {
  background-color: #40a9ff;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
}

.btn-secondary:hover {
  background-color: #d9d9d9;
}

.btn-danger {
  background-color: #ff4d4f;
  color: white;
}

.btn-danger:hover {
  background-color: #ff7875;
}

.edit-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.edit-dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.edit-dialog h4 {
  margin: 0 0 16px 0;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #666;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.dialog-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>
