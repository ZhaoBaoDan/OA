<template>
  <div class="notification-container">
    <el-card shadow="never" class="notification-card">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="activeTab" @change="handleTabChange">
            <el-radio-button value="all">全部</el-radio-button>
            <el-radio-button value="unread">
              未读
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="tab-badge" />
            </el-radio-button>
            <el-radio-button value="read">已读</el-radio-button>
          </el-radio-group>
          <el-button type="primary" link :icon="Check" @click="handleMarkAllRead" :disabled="unreadCount === 0">
            全部标记已读
          </el-button>
        </div>
      </template>

      <el-empty v-if="!notificationList.length && !loading" description="暂无通知" />

      <div v-else class="notification-list" v-loading="loading">
        <div
          v-for="item in notificationList"
          :key="item.id"
          class="notification-item"
          :class="{ unread: !item.isRead }"
          @click="handleRead(item)"
        >
          <div class="item-left">
            <el-icon :size="20" :style="{ color: getTypeColor(item.type) }">
              <component :is="getTypeIcon(item.type)" />
            </el-icon>
            <div v-if="!item.isRead" class="unread-dot" />
          </div>
          <div class="item-body">
            <div class="item-header">
              <span class="item-title">{{ item.title }}</span>
              <span class="item-time">{{ item.createTime }}</span>
            </div>
            <p class="item-content">{{ item.content }}</p>
          </div>
          <div class="item-actions" @click.stop>
            <el-dropdown trigger="click">
              <el-icon class="more-icon"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="!item.isRead" @click="handleMarkRead(item)">标记已读</el-dropdown-item>
                  <el-dropdown-item @click="handleDelete(item)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>

      <Pagination
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="loadNotifications"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, MoreFilled, Bell, Message, Warning, Promotion, Setting } from '@element-plus/icons-vue'
import { listNotification, getUnreadCount, markRead, markAllRead, deleteNotification } from '@/api/notification'
import type { NotificationItem } from '@/types/business'
import Pagination from '@/components/Pagination.vue'

const activeTab = ref('all')
const loading = ref(false)
const notificationList = ref<NotificationItem[]>([])
const unreadCount = ref(0)
const total = ref(0)

const queryParams = reactive({ pageNum: 1, pageSize: 20 })

function getTypeIcon(type: string) {
  const map: Record<string, any> = { system: Bell, workflow: Promotion, task: Warning, meeting: Message, attendance: Setting }
  return map[type] || Bell
}

function getTypeColor(type: string) {
  const map: Record<string, string> = { system: '#409eff', workflow: '#67c23a', task: '#e6a23c', meeting: '#909399', attendance: '#f56c6c' }
  return map[type] || '#909399'
}

async function loadNotifications() {
  loading.value = true
  try {
    const res = await listNotification(activeTab.value, queryParams.pageNum, queryParams.pageSize)
    notificationList.value = res.data.rows
    total.value = res.data.total
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data
  } catch { /* handled by interceptor */ }
}

function handleTabChange() {
  queryParams.pageNum = 1
  loadNotifications()
}

async function handleRead(item: NotificationItem) {
  if (!item.isRead) {
    try {
      await markRead(item.id)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch { /* handled by interceptor */ }
  }
}

async function handleMarkRead(item: NotificationItem) {
  try {
    await markRead(item.id)
    item.isRead = 1
    unreadCount.value = Math.max(0, unreadCount.value - 1)
    ElMessage.success('已标记为已读')
  } catch { /* handled by interceptor */ }
}

async function handleMarkAllRead() {
  try {
    await markAllRead()
    notificationList.value.forEach(n => (n.isRead = 1))
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch { /* handled by interceptor */ }
}

async function handleDelete(item: NotificationItem) {
  await ElMessageBox.confirm('确定要删除该通知吗？', '提示', { type: 'warning' })
  try {
    await deleteNotification(item.id)
    ElMessage.success('删除成功')
    loadNotifications()
    loadUnreadCount()
  } catch { /* handled by interceptor */ }
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.notification-container { max-width: 800px; margin: 0 auto; }
.notification-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.tab-badge { margin-left: 6px; }

.notification-list { display: flex; flex-direction: column; }

.notification-item {
  display: flex; gap: 14px; padding: 16px 12px; border-bottom: 1px solid #f0f0f0;
  cursor: pointer; transition: background 0.2s;
  &:hover { background: #f5f7fa; }
  &:last-child { border-bottom: none; }
  &.unread { background: #ecf5ff; &:hover { background: #d9ecff; } .item-title { font-weight: 600; } }
}

.item-left {
  position: relative; padding-top: 2px;
  .unread-dot { position: absolute; top: 0; right: -3px; width: 8px; height: 8px; border-radius: 50%; background: #f56c6c; }
}

.item-body { flex: 1; min-width: 0; }

.item-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;
  .item-title { font-size: 14px; color: #303133; }
  .item-time { font-size: 12px; color: #c0c4cc; flex-shrink: 0; }
}

.item-content { font-size: 13px; color: #909399; margin: 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.item-actions {
  display: flex; align-items: flex-start;
  .more-icon { cursor: pointer; color: #c0c4cc; font-size: 16px; &:hover { color: #409eff; } }
}
</style>
