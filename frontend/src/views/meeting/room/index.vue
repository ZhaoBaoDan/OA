<template>
  <div class="meeting-container">
    <el-row :gutter="16">
      <!-- 左侧：会议室列表 -->
      <el-col :span="7">
        <el-card shadow="never" class="room-list-card">
          <template #header>
            <div class="panel-header">
              <span>会议室列表</span>
              <el-button type="primary" :icon="Plus" size="small" @click="handleAddRoom">新增</el-button>
            </div>
          </template>
          <div class="room-cards">
            <div
              v-for="room in roomList"
              :key="room.id"
              class="room-card"
              :class="{ active: selectedRoom?.id === room.id }"
              @click="selectRoom(room)"
            >
              <div class="room-header">
                <span class="room-name">{{ room.name }}</span>
                <el-tag :type="room.status === '0' ? 'success' : 'danger'" size="small">
                  {{ room.status === '0' ? '可用' : '维护中' }}
                </el-tag>
              </div>
              <div class="room-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>容纳 {{ room.capacity }} 人</span>
                </div>
                <div class="info-item">
                  <el-icon><Monitor /></el-icon>
                  <span>{{ room.equipment.join('、') }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ room.location }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：日历与预约 -->
      <el-col :span="17">
        <el-card shadow="never" class="calendar-card">
          <template #header>
            <div class="panel-header">
              <span>{{ selectedRoom ? `${selectedRoom.name} - 预约日历` : '请选择会议室' }}</span>
            </div>
          </template>

          <el-empty v-if="!selectedRoom" description="请在左侧选择会议室" />

          <template v-else>
            <el-calendar v-model="currentDate">
              <template #date-cell="{ data }">
                <div class="calendar-cell" @click="handleDateClick(data.day)">
                  <span class="day-number">{{ data.day.split('-')[2] }}</span>
                  <div v-if="getDateBookings(data.day).length" class="booking-dots">
                    <span
                      v-for="(b, i) in getDateBookings(data.day).slice(0, 3)"
                      :key="i"
                      class="booking-dot"
                      :style="{ background: b.color }"
                      :title="`${b.timeRange} - ${b.title}`"
                    />
                    <span v-if="getDateBookings(data.day).length > 3" class="more-count">
                      +{{ getDateBookings(data.day).length - 3 }}
                    </span>
                  </div>
                </div>
              </template>
            </el-calendar>

            <!-- 当天预约时间轴 -->
            <div v-if="selectedDate" class="daily-bookings">
              <h4>{{ selectedDate }} 预约详情</h4>
              <el-timeline v-if="getDateBookings(selectedDate).length">
                <el-timeline-item
                  v-for="(booking, idx) in getDateBookings(selectedDate)"
                  :key="idx"
                  :timestamp="booking.timeRange"
                  :color="booking.color"
                  placement="top"
                >
                  <el-card shadow="hover" class="booking-item-card">
                    <div class="booking-item">
                      <div>
                        <strong>{{ booking.title }}</strong>
                        <p class="booking-organizer">组织者：{{ booking.organizer }}</p>
                      </div>
                      <el-tag size="small" :style="{ background: booking.color, color: '#fff', border: 'none' }">
                        {{ booking.timeRange }}
                      </el-tag>
                    </div>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="当日暂无预约" :image-size="60" />
              <el-button type="primary" :icon="Plus" class="add-booking-btn" @click="handleAddBooking">新增预约</el-button>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <!-- 预约弹窗 -->
    <el-dialog v-model="bookingDialogVisible" title="会议室预约" width="500px" destroy-on-close>
      <el-form ref="bookingFormRef" :model="bookingForm" :rules="bookingFormRules" label-width="80px">
        <el-form-item label="会议室" prop="roomId">
          <el-select v-model="bookingForm.roomId" placeholder="请选择会议室" style="width: 100%">
            <el-option v-for="room in roomList" :key="room.id" :label="room.name" :value="room.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="bookingForm.date" type="date" placeholder="请选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeRange">
          <el-time-picker
            v-model="bookingForm.timeRange"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="会议主题" prop="title">
          <el-input v-model="bookingForm.title" placeholder="请输入会议主题" />
        </el-form-item>
        <el-form-item label="参会人" prop="attendees">
          <el-input v-model="bookingForm.attendees" placeholder="多个参会人用逗号分隔" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="bookingForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBookingSubmit">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, User, Monitor, Location } from '@element-plus/icons-vue'

interface Room {
  id: number
  name: string
  capacity: number
  equipment: string[]
  location: string
  status: string
}

interface Booking {
  id: number
  roomId: number
  date: string
  timeRange: string
  title: string
  organizer: string
  color: string
}

const roomList = ref<Room[]>([
  { id: 1, name: 'A101-大会议室', capacity: 20, equipment: ['投影仪', '白板', '视频会议'], location: 'A栋1楼', status: '0' },
  { id: 2, name: 'A202-小会议室', capacity: 8, equipment: ['电视', '白板'], location: 'A栋2楼', status: '0' },
  { id: 3, name: 'B301-培训室', capacity: 50, equipment: ['投影仪', '音响', '麦克风'], location: 'B栋3楼', status: '0' },
  { id: 4, name: 'C105-VIP接待室', capacity: 10, equipment: ['投影仪', '视频会议', '茶水'], location: 'C栋1楼', status: '1' },
])

const selectedRoom = ref<Room | null>(null)
const currentDate = ref(new Date().toISOString().split('T')[0])
const selectedDate = ref('')

const bookingList = ref<Booking[]>([
  { id: 1, roomId: 1, date: '2024-05-25', timeRange: '09:00-10:30', title: '产品需求评审会', organizer: '张三', color: '#409eff' },
  { id: 2, roomId: 1, date: '2024-05-25', timeRange: '14:00-15:00', title: '周例会', organizer: '李四', color: '#67c23a' },
  { id: 3, roomId: 1, date: '2024-05-26', timeRange: '10:00-11:30', title: '技术方案讨论', organizer: '王五', color: '#e6a23c' },
  { id: 4, roomId: 2, date: '2024-05-25', timeRange: '15:00-16:00', title: '面试', organizer: '赵六', color: '#f56c6c' },
])

const bookingDialogVisible = ref(false)
const bookingFormRef = ref<FormInstance>()
const bookingForm = ref({
  roomId: undefined as number | undefined,
  date: '',
  timeRange: [] as string[],
  title: '',
  attendees: '',
  remark: '',
})

const bookingFormRules: FormRules = {
  roomId: [{ required: true, message: '请选择会议室', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  timeRange: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  title: [{ required: true, message: '请输入会议主题', trigger: 'blur' }],
}

function selectRoom(room: Room) {
  selectedRoom.value = room
  selectedDate.value = ''
}

function getDateBookings(date: string): Booking[] {
  if (!selectedRoom.value) return []
  return bookingList.value.filter(b => b.roomId === selectedRoom.value!.id && b.date === date)
}

function handleDateClick(date: string) {
  selectedDate.value = date
}

function handleAddRoom() {
  ElMessage.info('新增会议室功能开发中')
}

function handleAddBooking() {
  bookingForm.value = {
    roomId: selectedRoom.value?.id,
    date: selectedDate.value,
    timeRange: [],
    title: '',
    attendees: '',
    remark: '',
  }
  bookingDialogVisible.value = true
}

function handleBookingSubmit() {
  if (!bookingFormRef.value) return
  bookingFormRef.value.validate((valid) => {
    if (!valid) return
    const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb']
    bookingList.value.push({
      id: Date.now(),
      roomId: bookingForm.value.roomId!,
      date: bookingForm.value.date,
      timeRange: Array.isArray(bookingForm.value.timeRange)
        ? `${bookingForm.value.timeRange[0]}-${bookingForm.value.timeRange[1]}`
        : bookingForm.value.timeRange,
      title: bookingForm.value.title,
      organizer: bookingForm.value.attendees.split(',')[0] || '我',
      color: colors[Math.floor(Math.random() * colors.length)],
    })
    ElMessage.success('预约成功')
    bookingDialogVisible.value = false
  })
}
</script>

<style lang="scss" scoped>
.meeting-container {
  height: calc(100vh - 110px);
}

.room-list-card {
  border-radius: 8px;
  height: 100%;

  :deep(.el-card__body) {
    padding: 12px;
    height: calc(100% - 60px);
    overflow-y: auto;
  }
}

.calendar-card {
  border-radius: 8px;
  height: 100%;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.room-card {
  padding: 14px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  }

  &.active {
    border-color: #409eff;
    background: #ecf5ff;
  }
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  .room-name {
    font-weight: 600;
    font-size: 14px;
    color: #303133;
  }
}

.room-info {
  display: flex;
  flex-direction: column;
  gap: 6px;

  .info-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #909399;

    .el-icon {
      font-size: 14px;
    }
  }
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  cursor: pointer;

  .day-number {
    font-size: 14px;
  }
}

.booking-dots {
  display: flex;
  gap: 3px;
  flex-wrap: wrap;
  justify-content: center;
}

.booking-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.more-count {
  font-size: 10px;
  color: #909399;
}

.daily-bookings {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;

  h4 {
    margin: 0 0 16px;
    font-size: 15px;
    color: #303133;
  }
}

.booking-item-card {
  :deep(.el-card__body) {
    padding: 10px 14px;
  }
}

.booking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;

  strong {
    font-size: 14px;
  }

  .booking-organizer {
    margin: 4px 0 0;
    font-size: 12px;
    color: #909399;
  }
}

.add-booking-btn {
  margin-top: 16px;
}
</style>
